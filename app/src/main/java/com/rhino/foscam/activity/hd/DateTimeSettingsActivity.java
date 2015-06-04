package com.rhino.foscam.activity.hd;

import java.util.ArrayList;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.hd.SystemTime;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class DateTimeSettingsActivity extends Activity{

    public String displayedTime;
    public boolean ntpEnabled;
    public String ntpServer;
    public Integer dateFormat;
    public Integer timeFormat;
    public Integer timeZone;
    public boolean dstEnabled;

    private DateTimeSettingsActivity context = this;
    private final Camera camera = new Camera();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_settings_hd);

        Intent intent = getIntent();

        camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
        camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
        camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
        camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
        camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));

        displayedTime = intent.getStringExtra(CameraMenuActivity.SYSTEM_TIME_EXTRA);
        ntpEnabled = intent.getBooleanExtra(CameraMenuActivity.TIME_SOURCE_EXTRA, true);
        ntpServer = intent.getStringExtra(CameraMenuActivity.NTP_SERVER_EXTRA);
        dateFormat = intent.getIntExtra(CameraMenuActivity.DATE_FORMAT_EXTRA, 0);
        timeFormat = intent.getIntExtra(CameraMenuActivity.TIME_FORMAT_EXTRA, 0);
        timeZone = intent.getIntExtra(CameraMenuActivity.TIME_ZONE_EXTRA, 0);
        dstEnabled = intent.getBooleanExtra(CameraMenuActivity.DST_EXTRA, false);

        int timeZoneIndex = ((timeZone / 3600) * -1) + 11;
        Spinner timeZoneSpinner = (Spinner)findViewById(R.id.timeZoneList);
        timeZoneSpinner.setSelection(timeZoneIndex);

        TextView deviceTime = (TextView)findViewById(R.id.deviceClock);
        deviceTime.setText(displayedTime);

        CheckBox ntpEnable = (CheckBox)findViewById(R.id.syncNTP);
        ntpEnable.setChecked(ntpEnabled);

        if(!ntpEnabled) {
            findViewById(R.id.ntpServerRow).setVisibility(View.GONE);
        }

        final Spinner ntpServerSpinner = (Spinner)findViewById(R.id.ntpServerList);
        ntpServerSpinner.setSelection(getServerIndex(ntpServer));

        final Spinner dateFormatSpinner = (Spinner)findViewById(R.id.dateFormatList);
        dateFormatSpinner.setSelection(dateFormat);

        final Spinner timeFormatSpinner = (Spinner)findViewById(R.id.timeFormatList);
        timeFormatSpinner.setSelection(timeFormat);

        CheckBox daylightSaving = (CheckBox)findViewById(R.id.daylightSavingsTime);
        daylightSaving.setChecked(dstEnabled);

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
        Spinner dateFormatSpinner = (Spinner)findViewById(R.id.dateFormatList);
        Spinner timeFormatSpinner = (Spinner)findViewById(R.id.timeFormatList);

        int timeZone = ((timeZoneSpinner.getSelectedItemPosition()) - 11) * -3600;

        SystemTime systemTime = new SystemTime();
        systemTime.setTimeSource(ntpEnable.isChecked() ? "0" : "1");
        systemTime.setNtpServer(ntpServers().get(ntpServerSpinner.getSelectedItemPosition()));
        systemTime.setTimeZone(String.valueOf(timeZone));
        systemTime.setDateFormat(String.valueOf(dateFormatSpinner.getSelectedItemPosition()));
        systemTime.setTimeFormat(String.valueOf(timeFormatSpinner.getSelectedItemPosition()));
        systemTime.setIsDst(daylightSaving.isChecked() ? "1" : "0");

        com.rhino.foscam.task.hd.SetDateTimeTask task = new com.rhino.foscam.task.hd.SetDateTimeTask(context, camera, systemTime);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
