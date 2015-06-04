package com.rhino.foscam.activity.hd;

import java.io.File;
import java.util.ArrayList;

import com.rhino.foscam.R;
import com.rhino.foscam.AlarmSnapshotsActivity;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.NotificationSettingsActivity;
import com.rhino.foscam.accessor.StorageUtils;
import com.rhino.foscam.adapter.CameraOptionListAdapter;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.CameraOption;
import com.rhino.foscam.pojo.hd.DevInfo;
import com.rhino.foscam.pojo.hd.FTP;
import com.rhino.foscam.pojo.hd.SystemTime;
import com.rhino.foscam.service.AlarmService;
import com.rhino.foscam.task.hd.CameraAliasTask;
import com.rhino.foscam.task.hd.CameraLogTask;
import com.rhino.foscam.task.hd.DateTimeSettingsTask;
import com.rhino.foscam.task.hd.FTPSettingsTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraMenuActivity extends Activity {
	
	private final ArrayList<CameraOption> optionList = new ArrayList<CameraOption>();
	private CameraOptionListAdapter adapter;
	private ListView listView;
	private final CameraMenuActivity context = this;
	private final Camera camera = new Camera();
	
	private SharedPreferences prefs; 
	private SharedPreferences.Editor editor;
	
	public static final String LOG_EXTRA = "cameraLogExtra";
	public static final String ALIAS_EXTRA = "cameraAliasExtra";
    public static final String TIME_SOURCE_EXTRA = "timeSourceExtra";
    public static final String NTP_SERVER_EXTRA = "ntpServerExtra";
    public static final String DATE_FORMAT_EXTRA = "dateFormatExtra";
    public static final String TIME_FORMAT_EXTRA = "timeFormatExtra";
    public static final String TIME_ZONE_EXTRA = "timeZoneExtra";
    public static final String DST_EXTRA = "dstExtra";
    public static final String SYSTEM_TIME_EXTRA = "systemTimeExtra";
    public static final String FTP_ADDRESS_EXTRA = "ftpAddressExtra";
    public static final String FTP_PORT_EXTRA = "ftpPortExtra";
    public static final String FTP_MODE_EXTRA = "ftpModeExtra";
    public static final String FTP_USERNAME_EXTRA = "ftpUserExtra";
    public static final String FTP_PASSWORD_EXTRA = "ftpPasswordExtra";
	
	private static final String CAMERA_LOG = "Camera Log";
	private static final String CAMERA_ALIAS = "Camera Name";
    private static final String DATE_TIME_SETTING = "Date and Time Settings";
    private static final String FTP_SETTINGS = "FTP Settings";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_menu);
		
		prefs = getApplicationContext().getSharedPreferences(AlarmService.ALARM_SERVICE_PREF, Context.MODE_PRIVATE);
		editor = prefs.edit();
		
		LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout header = (LinearLayout)inflater.inflate(R.layout.camera_menu_header, null, false);
				
		adapter = new CameraOptionListAdapter(context, optionList);
		listView = (ListView)findViewById(R.id.cameraOptionsList);
		listView.addHeaderView(header);
		listView.setAdapter(adapter);
		
		Intent intent = getIntent();
		
		TextView cameraNameText = (TextView)findViewById(R.id.selectedCameraName);
		cameraNameText.setText(intent.getStringExtra(MainMenuActivity.CAMERA_NAME_EXTRA));
		
		camera.setCameraName(intent.getStringExtra(MainMenuActivity.CAMERA_NAME_EXTRA));
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));

        optionList.add(new CameraOption(FTP_SETTINGS));
        optionList.add(new CameraOption(DATE_TIME_SETTING));
		optionList.add(new CameraOption(CAMERA_ALIAS));
		optionList.add(new CameraOption(CAMERA_LOG));
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int optionIndex, long arg3) {
				startSelectedActivityTask(optionList.get(optionIndex - 1).getOptionName());
			}
		});
		
		CheckBox enableNotifications = (CheckBox)header.findViewById(R.id.alarmNotification);
		enableNotifications.setChecked(isNotificationsEnabled());
		
		enableNotifications.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				if(checked) {
					startNotificationService();
					Toast.makeText(context, "Notifications Enabled", Toast.LENGTH_SHORT).show();
				} else {
					stopNotificationService();
					Toast.makeText(context, "Notifications Disabled", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		ImageButton helpButton = (ImageButton)header.findViewById(R.id.helpButton);
		helpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showNotificationHelpDialog();
			}
		});
		
		String htmlButtonText;
		int snapshotAlarmCount = getSnapshotCount();
		if(snapshotAlarmCount == 0) {
			htmlButtonText = "<font color='#FFFFFF'>Alarm Snapshots</font>";
		} else {
			htmlButtonText = "<font color='#FFFFFF'>Alarm Snapshots </font><font color='#0099CC'>(" + snapshotAlarmCount + ")</font>"; 
		}
		Button galleryButton = (Button)findViewById(R.id.openGalleryButton);
		galleryButton.setText(Html.fromHtml(htmlButtonText));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Rate");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.rhino.foscam"));
		startActivity(intent);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	public void returnToMainMenu(View v) {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void startSelectedActivityTask(String activityName) {
		if(activityName.equals(CAMERA_ALIAS)) {
			CameraAliasTask task = new CameraAliasTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(CAMERA_LOG)) {
			CameraLogTask task = new CameraLogTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(DATE_TIME_SETTING)) {
            DateTimeSettingsTask task = new DateTimeSettingsTask(context, camera);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if(activityName.equals(FTP_SETTINGS)) {
            FTPSettingsTask task = new FTPSettingsTask(context, camera);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
	}
	
	public void startCameraLog(String log) {
		Intent intent = new Intent(context, CameraLogActivity.class);
		intent.putExtra(LOG_EXTRA, log);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void startCameraAlias(DevInfo devInfo) {
		Intent intent = new Intent(context, CameraAliasActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(ALIAS_EXTRA, devInfo.getCameraName());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}

    public void startDateTimeActivity(SystemTime systemTime) {
        Intent intent = new Intent(context, DateTimeSettingsActivity.class);
        intent.putExtra(SYSTEM_TIME_EXTRA, systemTime.getDisplayedTime());
        intent.putExtra(TIME_SOURCE_EXTRA, systemTime.isNtpEnabled());
        intent.putExtra(NTP_SERVER_EXTRA, systemTime.getNtpServer());
        intent.putExtra(DATE_FORMAT_EXTRA, systemTime.getDateFormat());
        intent.putExtra(TIME_FORMAT_EXTRA, systemTime.getTimeFormat());
        intent.putExtra(TIME_ZONE_EXTRA, systemTime.getTimeZone());
        intent.putExtra(DST_EXTRA, systemTime.isDst());
        intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
        intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
        intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
        intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
        intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void startFTPSettingsActivity(FTP ftp) {
        Intent intent = new Intent(context, FTPSettingsActivity.class);
        intent.putExtra(FTP_ADDRESS_EXTRA, ftp.getFtpAddr());
        intent.putExtra(FTP_PORT_EXTRA, ftp.getFtpPort());
        intent.putExtra(FTP_MODE_EXTRA, ftp.getModeIndex());
        intent.putExtra(FTP_USERNAME_EXTRA, ftp.getUserName());
        intent.putExtra(FTP_PASSWORD_EXTRA, ftp.getPassword());
        intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
        intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
        intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
        intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
        intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
	
	public void startAlarmSnapshotsActivity() {
		Intent intent = new Intent(context, AlarmSnapshotsActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_NAME_EXTRA, camera.getCameraName());
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		startActivity(intent);
	}
	
	public void showNotificationHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alarm Notifications");
        builder.setMessage("Checking this option will cause a notification to display and optionally save snapshots when the alarm is triggered for this camera.  Go to 'Notification Settings' to set your notification preferences.");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        builder.create().show();
	}
	
	public boolean isNotificationsEnabled() {
		String activeCameras = prefs.getString(AlarmService.CAMERA_LIST, "");
		ArrayList<String> cameraNames = new ArrayList<String>();
		String[] separated = activeCameras.split(MainMenuActivity.CAMERA_SEPARATOR);
		
		for(String activeCamera : separated) {
			String[] fields = activeCamera.split(MainMenuActivity.FIELD_SEPARATOR);
			cameraNames.add(fields[0]);
		}
		
		return cameraNames.contains(camera.getCameraName());
	}
	
	public void startNotificationService() {
		String cameraString = camera.getCameraName() + MainMenuActivity.FIELD_SEPARATOR +
								camera.getCameraUrl() + MainMenuActivity.FIELD_SEPARATOR +
								camera.getPort() + MainMenuActivity.FIELD_SEPARATOR +
								camera.getUsername() + MainMenuActivity.FIELD_SEPARATOR +
								camera.getPassword() + MainMenuActivity.FIELD_SEPARATOR +
								camera.getType() + MainMenuActivity.CAMERA_SEPARATOR;
		String activeCameras = prefs.getString(AlarmService.CAMERA_LIST, "");
		activeCameras = activeCameras + cameraString;
		editor.putString(AlarmService.CAMERA_LIST, activeCameras);
		editor.commit();
		
		notifyService();
	}
	
	public void stopNotificationService() {
		String oldCameraString = prefs.getString(AlarmService.CAMERA_LIST, "");
		String newCameraString = "";
		
		String[] cameraStrings = oldCameraString.split(MainMenuActivity.CAMERA_SEPARATOR);
		
		for(String cameraString : cameraStrings) {
			String[] cameraFields = cameraString.split(MainMenuActivity.FIELD_SEPARATOR);
			
			if(!camera.getCameraName().equals(cameraFields[0])) {
				newCameraString = newCameraString + cameraString + MainMenuActivity.CAMERA_SEPARATOR;
			}
		}
		
		editor.putString(AlarmService.CAMERA_LIST, newCameraString);
		editor.commit();
		
		notifyService();
	}
	
	private int getSnapshotCount() {
		SharedPreferences notificationPrefs = context.getSharedPreferences(NotificationSettingsActivity.NOTIFICATION_PREF, Context.MODE_PRIVATE);
		String snapshotCountString = notificationPrefs.getString(NotificationSettingsActivity.SNAPSHOT_COUNT, "");
		String[] snapshotCounts = snapshotCountString.split(MainMenuActivity.CAMERA_SEPARATOR);
		
		for(String count : snapshotCounts) {
			String[] temp = count.split(MainMenuActivity.FIELD_SEPARATOR);
			if(camera.getCameraName().equals(temp[0])) {
				return Integer.parseInt(temp[1]);
			}
		}
		return 0;
	}
	
	private void resetSnapshotCount() {
		SharedPreferences notificationPrefs = context.getSharedPreferences(NotificationSettingsActivity.NOTIFICATION_PREF, Context.MODE_PRIVATE);
		String oldSnapshotCountString = notificationPrefs.getString(NotificationSettingsActivity.SNAPSHOT_COUNT, "");
		String newSnapshotCountString = "";
		String[] snapshotCounts = oldSnapshotCountString.split(MainMenuActivity.CAMERA_SEPARATOR);
		
		for(String snapshotCount : snapshotCounts) {
			String[] temp = snapshotCount.split(MainMenuActivity.FIELD_SEPARATOR);
			if(!camera.getCameraName().equals(temp[0])) {
				newSnapshotCountString = newSnapshotCountString + snapshotCount + MainMenuActivity.CAMERA_SEPARATOR;
			}
		}

		SharedPreferences.Editor editor = notificationPrefs.edit();
		editor.putString(NotificationSettingsActivity.SNAPSHOT_COUNT, newSnapshotCountString);
		editor.commit();
	}
	
	public void openGallery(View v) {
		String htmlButtonText = "<font color='#FFFFFF'>Alarm Snapshots</font>";
		((Button)v).setText(Html.fromHtml(htmlButtonText));
		resetSnapshotCount();
		
		String dir = StorageUtils.PICTURE_DIR + File.separator + camera.getCameraName();
		File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
		folder.mkdirs();
		
		String[] files = folder.list();
		
		if(files.length > 0) {		
			startAlarmSnapshotsActivity();
		} else {
			Toast.makeText(context, "No alarm snapshots found for this camera", Toast.LENGTH_LONG).show();
		}
	}
	
	public void notifyService() {
		Intent intent = new Intent(this, AlarmService.class);
		startService(intent);
	}

}
