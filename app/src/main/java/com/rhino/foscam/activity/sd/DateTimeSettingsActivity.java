package com.rhino.foscam.activity.sd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.task.sd.SetDateTimeTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DateTimeSettingsActivity extends Activity{
	
	public long epoch;
	public int dst;
	public boolean ntpEnabled;
	public String ntpServer;
	public int timeZone;
	private DateTimeSettingsActivity context = this;
	private final Camera camera = new Camera();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_time_settings);
		
		Intent intent = getIntent();
		
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		
		epoch = intent.getLongExtra(CameraMenuActivity.EPOCH_EXTRA, 0);
		dst = intent.getIntExtra(CameraMenuActivity.DST_EXTRA, 0);
		ntpEnabled = intent.getBooleanExtra(CameraMenuActivity.NTP_ENABLED_EXTRA, true);
		ntpServer = intent.getStringExtra(CameraMenuActivity.NTP_SERVER_EXTRA);
		timeZone = intent.getIntExtra(CameraMenuActivity.TIMEZONE_EXTRA, 0);
		
		int timeZoneIndex = ((timeZone / 3600) * -1) + 11;
		Spinner timeZoneSpinner = (Spinner)findViewById(R.id.timeZoneList);
		timeZoneSpinner.setSelection(timeZoneIndex);
		
		Date date = new Date((epoch - timeZone - dst) * 1000);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy   h:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String formattedDate = sdf.format(calendar.getTime());
		
		TextView deviceTime = (TextView)findViewById(R.id.deviceClock);
		deviceTime.setText(formattedDate);
		
		CheckBox ntpEnable = (CheckBox)findViewById(R.id.syncNTP);
		ntpEnable.setChecked(ntpEnabled);
		
		if(!ntpEnabled) {
			findViewById(R.id.ntpServerRow).setVisibility(View.GONE);
		}
		
		final Spinner ntpServerSpinner = (Spinner)findViewById(R.id.ntpServerList);
		ntpServerSpinner.setSelection(getServerIndex(ntpServer));
		
		CheckBox daylightSaving = (CheckBox)findViewById(R.id.daylightSavingsTime);
		daylightSaving.setChecked(dst != 0);
		
		ntpEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					findViewById(R.id.ntpServerRow).setVisibility(View.VISIBLE);
					ntpServerSpinner.setSelection(0);
				} else { 
					findViewById(R.id.ntpServerRow).setVisibility(View.GONE);
				}
			}
		});
	}
	
	public ArrayList<String> ntpServers() {
		ArrayList<String> servers = new ArrayList<String>();
		servers.add("time.nist.gov");
		servers.add("time.kriss.re.kr");
		servers.add("time.windows.com");
		servers.add("time.nuri.net");
		return servers;
	}
	
	public int getServerIndex(String server) {
		ArrayList<String> servers = ntpServers();
		if(servers.contains(server)) {
			return servers.indexOf(server);
		} else {
			return 0;
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		finish();
	}
	
	public void returnToCameraMenu(View v) {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void saveComplete() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void saveChanges(View v) {
		Spinner timeZoneSpinner = (Spinner)findViewById(R.id.timeZoneList);
		CheckBox ntpEnable = (CheckBox)findViewById(R.id.syncNTP);
		Spinner ntpServerSpinner = (Spinner)findViewById(R.id.ntpServerList);
		CheckBox daylightSaving = (CheckBox)findViewById(R.id.daylightSavingsTime);
		
		int timeZone = ((timeZoneSpinner.getSelectedItemPosition()) - 11) * -3600;
		boolean ntpSync = ntpEnable.isChecked();
		String ntpServer = "";
		int dst = 0;
		
		if(daylightSaving.isChecked()) {
			dst = -3600;
		}
		
		if(ntpSync) {
			ntpServer = ntpServers().get(ntpServerSpinner.getSelectedItemPosition());
		}
		
		CameraParams params = new CameraParams();
		params.setTimeZone(timeZone);
		params.setNtpEnable(ntpSync);
		params.setNtpServer(ntpServer);
		params.setDst(dst);
		
		SetDateTimeTask task = new SetDateTimeTask(context, camera, params);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
