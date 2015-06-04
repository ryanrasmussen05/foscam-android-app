package com.rhino.foscam.activity.sd;

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
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.pojo.sd.CameraStatus;
import com.rhino.foscam.pojo.sd.MiscParams;
import com.rhino.foscam.service.AlarmService;
import com.rhino.foscam.task.sd.AlarmSettingsTask;
import com.rhino.foscam.task.sd.CameraAliasTask;
import com.rhino.foscam.task.sd.CameraLogTask;
import com.rhino.foscam.task.sd.DateTimeSettingsTask;
import com.rhino.foscam.task.sd.FTPSettingsTask;
import com.rhino.foscam.task.sd.MailSettingsTask;
import com.rhino.foscam.task.sd.ManageUsersTask;
import com.rhino.foscam.task.sd.PTSettingsTask;

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
	private final Camera camera = new Camera();;
	
	private SharedPreferences prefs; 
	private SharedPreferences.Editor editor;
	
	public static final int MANAGE_USERS_CODE = 1111;
	public static final int PT_SETTINGS_CODE = 2222;
	
	public static final String LOG_EXTRA = "cameraLogExtra";
	public static final String ALIAS_EXTRA = "cameraAliasExtra";
	public static final String EPOCH_EXTRA = "epochTimeExtra";
	public static final String DST_EXTRA = "daylightSavingExtra";
	public static final String NTP_ENABLED_EXTRA = "ntpServerEnabledExtra";
	public static final String NTP_SERVER_EXTRA = "ntpServerExtra";
	public static final String TIMEZONE_EXTRA = "timezoneExtra";
	public static final String USER_1_NAME_EXTRA = "user1NameExtra";
	public static final String USER_1_PASSWORD_EXTRA = "user1PasswordExtra";
	public static final String USER_1_PRIV_EXTRA = "user1PrivExtra";
	public static final String USER_2_NAME_EXTRA = "user2NameExtra";
	public static final String USER_2_PASSWORD_EXTRA = "user2PasswordExtra";
	public static final String USER_2_PRIV_EXTRA = "user2PrivExtra";
	public static final String USER_3_NAME_EXTRA = "user3NameExtra";
	public static final String USER_3_PASSWORD_EXTRA = "user3PasswordExtra";
	public static final String USER_3_PRIV_EXTRA = "user3PrivExtra";
	public static final String USER_4_NAME_EXTRA = "user4NameExtra";
	public static final String USER_4_PASSWORD_EXTRA = "user4PasswordExtra";
	public static final String USER_4_PRIV_EXTRA = "user4PrivExtra";
	public static final String USER_5_NAME_EXTRA = "user5NameExtra";
	public static final String USER_5_PASSWORD_EXTRA = "user5PasswordExtra";
	public static final String USER_5_PRIV_EXTRA = "user5PrivExtra";
	public static final String USER_6_NAME_EXTRA = "user6NameExtra";
	public static final String USER_6_PASSWORD_EXTRA = "user6PasswordExtra";
	public static final String USER_6_PRIV_EXTRA = "user6PrivExtra";
	public static final String USER_7_NAME_EXTRA = "user7NameExtra";
	public static final String USER_7_PASSWORD_EXTRA = "user7PasswordExtra";
	public static final String USER_7_PRIV_EXTRA = "user7PrivExtra";
	public static final String USER_8_NAME_EXTRA = "user8NameExtra";
	public static final String USER_8_PASSWORD_EXTRA = "user8passwordExtra";
	public static final String USER_8_PRIV_EXTRA = "user8PrivExtra";
	public static final String FTP_SERVER_EXTRA = "ftpServerExtra";
	public static final String FTP_PORT_EXTRA = "ftpPortExtra";
	public static final String FTP_USER_EXTRA = "ftpUserExtra";
	public static final String FTP_PASSWORD_EXTRA = "ftpPasswordExtra";
	public static final String FTP_FOLDER_EXTRA = "ftpFolderExtra";
	public static final String FTP_MODE_EXTRA = "ftpModeExtra";
	public static final String FTP_INTERVAL_EXTRA = "ftpIntervalExtra";
	public static final String FTP_FILENAME_EXTRA = "ftpFilenameExtra";
	public static final String MAIL_SERVER_EXTRA = "mailServerExtra";
	public static final String MAIL_PORT_EXTRA = "mailPortExtra";
	public static final String MAIL_MODE_EXTRA = "mailModeExtra";
	public static final String MAIL_USER_EXTRA = "mailUserExtra";
	public static final String MAIL_PASSWORD_EXTRA = "mailPasswordExtra";
	public static final String MAIL_SENDER_EXTRA = "mailSenderExtra";
	public static final String MAIL_RECEIVER_1_EXTRA = "mailReceiver1Extra";
	public static final String MAIL_RECEIVER_2_EXTRA = "mailReceiver2Extra";
	public static final String MAIL_RECEIVER_3_EXTRA = "mailReceiver3Extra";
	public static final String MAIL_RECEIVER_4_EXTRA = "mailReceiver4Extra";
	public static final String MAIL_REPORT_IP_EXTRA = "mailReportIPExtra";
	public static final String PT_DISABLE_PRESET_EXTRA = "ptDisablePresetExtra";
	public static final String PT_PRESET_BOOT_EXTRA = "ptPresetOnBootExtra";
	public static final String PT_CENTER_BOOT_EXTRA = "ptCenterOnBootExtra";
	public static final String PT_ROUNDS_VERTICAL_EXTRA = "ptRoundsVerticalExtra";
	public static final String PT_ROUNDS_HORIZONTAL_EXTRA = "ptRoundsHorizontalExtra";
	public static final String PT_PATROL_RATE_EXTRA = "ptPatrolRateExtra";
	public static final String PT_UPWARD_EXTRA = "ptUpwardExtra";
	public static final String PT_DOWNWARD_EXTRA = "ptDownwardExtra";
	public static final String PT_RIGHTWARD_EXTRA = "ptRightwardExtra";
	public static final String PT_LEFTWARD_EXTRA = "ptLeftwardExtra";
	public static final String MOTION_SENSITIVITY_EXTRA = "motionSensitivityExtra";
	public static final String SOUND_SENSITIVITY_EXTRA = "soundSenstivityExtra";
	public static final String MOTION_COMPENSATION_EXTRA = "motionCompensationExtra";
	public static final String MAIL_ALARM_EXTRA = "mailAlarmExtra";
	public static final String UPLOAD_ALARM_INTERVAL_EXTRA = "uploadAlarmExtra";
	public static final String ENABLE_SCHEDULER_EXTRA = "enableSchedulerExtra";
	public static final String MOTION_ENABLED_EXTRA = "motionEnabledExtra";
	public static final String SOUND_ENABLED_EXTRA = "soundEnabledExtra";
	
	private static final String ALARM_NOTIFICATION_SETTINGS = "Notification Settings";
	private static final String ALARM_SETTINGS = "Alarm Settings";
	private static final String PAN_TILT_SETTINGS = "Pan/Tilt Settings";
	private static final String MAIL_SERVICE = "Mail Service";
	private static final String FTP_SERVICE = "FTP Service";
	private static final String MANAGE_USERS = "Manage Users";
	private static final String DATE_TIME_SETTINGS = "Date and Time Settings";
	private static final String ALIAS_SETTINGS = "Camera Name";
	private static final String CAMERA_LOG = "Camera Log";
	
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
		
		optionList.add(new CameraOption(ALARM_NOTIFICATION_SETTINGS));
		optionList.add(new CameraOption(ALARM_SETTINGS));
		optionList.add(new CameraOption(PAN_TILT_SETTINGS));
		optionList.add(new CameraOption(MAIL_SERVICE));
		optionList.add(new CameraOption(FTP_SERVICE));
		optionList.add(new CameraOption(MANAGE_USERS));
		optionList.add(new CameraOption(DATE_TIME_SETTINGS));
		optionList.add(new CameraOption(ALIAS_SETTINGS));
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(((requestCode == MANAGE_USERS_CODE) || (requestCode == PT_SETTINGS_CODE)) && resultCode == 0) {
			Toast toast = Toast.makeText(context, "Camera rebooting.  Please wait 30 seconds before logging in.", Toast.LENGTH_LONG);
			toast.show();
			finish();
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
			super.onActivityResult(requestCode, resultCode, data);
		}
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
		if(activityName.equals(ALARM_SETTINGS)) {
			AlarmSettingsTask task = new AlarmSettingsTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(PAN_TILT_SETTINGS)) {
			PTSettingsTask task = new PTSettingsTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(MAIL_SERVICE)) {
			MailSettingsTask task = new MailSettingsTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(FTP_SERVICE)) {
			FTPSettingsTask task =  new FTPSettingsTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(MANAGE_USERS)) {
			ManageUsersTask task = new ManageUsersTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(DATE_TIME_SETTINGS)) {
			DateTimeSettingsTask task = new DateTimeSettingsTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(ALIAS_SETTINGS)) {
			CameraAliasTask task = new CameraAliasTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(CAMERA_LOG)) {
			CameraLogTask task = new CameraLogTask(context, camera);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else if(activityName.equals(ALARM_NOTIFICATION_SETTINGS)) {
			startNotificationSettingsActivity();
		}
	}
	
	public void startCameraLog(String log) {
		Intent intent = new Intent(context, CameraLogActivity.class);
		intent.putExtra(LOG_EXTRA, log);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void startCameraAlias(CameraStatus status) {
		Intent intent = new Intent(context, CameraAliasActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(ALIAS_EXTRA, status.getAlias());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void startDateTimeActivity(CameraParams cameraParams) {
		Intent intent = new Intent(context, DateTimeSettingsActivity.class);
		intent.putExtra(EPOCH_EXTRA, cameraParams.getEpoch());
		intent.putExtra(DST_EXTRA, cameraParams.getDst());
		intent.putExtra(NTP_ENABLED_EXTRA, cameraParams.isNtpEnabled());
		intent.putExtra(NTP_SERVER_EXTRA, cameraParams.getNtpServer());
		intent.putExtra(TIMEZONE_EXTRA, cameraParams.getTimeZone());
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void startManageUsersActivity(CameraParams cameraParams) {
		Intent intent = new Intent(context, ManageUsersActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(USER_1_NAME_EXTRA, cameraParams.getUser1Name());
		intent.putExtra(USER_1_PASSWORD_EXTRA, cameraParams.getUser1Password());
		intent.putExtra(USER_1_PRIV_EXTRA, cameraParams.getUser1Priv());
		intent.putExtra(USER_2_NAME_EXTRA, cameraParams.getUser2Name());
		intent.putExtra(USER_2_PASSWORD_EXTRA, cameraParams.getUser2Password());
		intent.putExtra(USER_2_PRIV_EXTRA, cameraParams.getUser2Priv());
		intent.putExtra(USER_3_NAME_EXTRA, cameraParams.getUser3Name());
		intent.putExtra(USER_3_PASSWORD_EXTRA, cameraParams.getUser3Password());
		intent.putExtra(USER_3_PRIV_EXTRA, cameraParams.getUser3Priv());
		intent.putExtra(USER_4_NAME_EXTRA, cameraParams.getUser4Name());
		intent.putExtra(USER_4_PASSWORD_EXTRA, cameraParams.getUser4Password());
		intent.putExtra(USER_4_PRIV_EXTRA, cameraParams.getUser4Priv());
		intent.putExtra(USER_5_NAME_EXTRA, cameraParams.getUser5Name());
		intent.putExtra(USER_5_PASSWORD_EXTRA, cameraParams.getUser5Password());
		intent.putExtra(USER_5_PRIV_EXTRA, cameraParams.getUser5Priv());
		intent.putExtra(USER_6_NAME_EXTRA, cameraParams.getUser6Name());
		intent.putExtra(USER_6_PASSWORD_EXTRA, cameraParams.getUser6Password());
		intent.putExtra(USER_6_PRIV_EXTRA, cameraParams.getUser6Priv());
		intent.putExtra(USER_7_NAME_EXTRA, cameraParams.getUser7Name());
		intent.putExtra(USER_7_PASSWORD_EXTRA, cameraParams.getUser7Password());
		intent.putExtra(USER_7_PRIV_EXTRA, cameraParams.getUser7Priv());
		intent.putExtra(USER_8_NAME_EXTRA, cameraParams.getUser8Name());
		intent.putExtra(USER_8_PASSWORD_EXTRA, cameraParams.getUser8Password());
		intent.putExtra(USER_8_PRIV_EXTRA, cameraParams.getUser8Priv());
		startActivityForResult(intent, MANAGE_USERS_CODE);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void startFTPSettingsActivity(CameraParams cameraParams) {
		Intent intent = new Intent(context, FTPSettingsActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(FTP_SERVER_EXTRA, cameraParams.getFtpServer());
		intent.putExtra(FTP_PORT_EXTRA, cameraParams.getFtpPort());
		intent.putExtra(FTP_USER_EXTRA, cameraParams.getFtpUser());
		intent.putExtra(FTP_PASSWORD_EXTRA, cameraParams.getFtpPassword());
		intent.putExtra(FTP_FOLDER_EXTRA, cameraParams.getFtpFolder());
		intent.putExtra(FTP_MODE_EXTRA, cameraParams.getFtpMode());
		intent.putExtra(FTP_INTERVAL_EXTRA, cameraParams.getFtpUploadInterval());
		intent.putExtra(FTP_FILENAME_EXTRA, cameraParams.getFtpFilename());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
	public void startMailSettingsActivity(CameraParams cameraParams) {
		Intent intent = new Intent(context, MailSettingsActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(MAIL_SERVER_EXTRA, cameraParams.getMailServer());
		intent.putExtra(MAIL_PORT_EXTRA, cameraParams.getMailPort());
		intent.putExtra(MAIL_MODE_EXTRA, cameraParams.getMailMode());
		intent.putExtra(MAIL_USER_EXTRA, cameraParams.getMailUser());
		intent.putExtra(MAIL_PASSWORD_EXTRA, cameraParams.getMailPassword());
		intent.putExtra(MAIL_SENDER_EXTRA, cameraParams.getMailSender());
		intent.putExtra(MAIL_RECEIVER_1_EXTRA, cameraParams.getMailReceiver1());
		intent.putExtra(MAIL_RECEIVER_2_EXTRA, cameraParams.getMailReceiver2());
		intent.putExtra(MAIL_RECEIVER_3_EXTRA, cameraParams.getMailReceiver3());
		intent.putExtra(MAIL_RECEIVER_4_EXTRA, cameraParams.getMailReceiver4());
		intent.putExtra(MAIL_REPORT_IP_EXTRA, cameraParams.isMailReportIP());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);		
	}
	
	public void startPTSettingsActivity(MiscParams miscParams) {
		Intent intent = new Intent(context, PTSettingsActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(PT_DISABLE_PRESET_EXTRA, miscParams.isDisablePreset());
		intent.putExtra(PT_PRESET_BOOT_EXTRA, miscParams.isPresetOnBoot());
		intent.putExtra(PT_CENTER_BOOT_EXTRA, miscParams.isCenterOnBoot());
		intent.putExtra(PT_ROUNDS_VERTICAL_EXTRA, miscParams.getPatrolRoundsVertical());
		intent.putExtra(PT_ROUNDS_HORIZONTAL_EXTRA, miscParams.getPatrolRoundsHorizontal());
		intent.putExtra(PT_PATROL_RATE_EXTRA, miscParams.getPatrolRate());
		intent.putExtra(PT_UPWARD_EXTRA, miscParams.getUpwardRate());
		intent.putExtra(PT_DOWNWARD_EXTRA, miscParams.getDownwardRate());
		intent.putExtra(PT_RIGHTWARD_EXTRA, miscParams.getRightwaredRate());
		intent.putExtra(PT_LEFTWARD_EXTRA, miscParams.getLeftwardRate());
		startActivityForResult(intent, PT_SETTINGS_CODE);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
	}
	
	public void startAlarmSettingsActivity(CameraParams params) {
		Intent intent = new Intent(context, AlarmSettingsActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		intent.putExtra(MOTION_SENSITIVITY_EXTRA, params.getMotionSensitivity());
		intent.putExtra(SOUND_SENSITIVITY_EXTRA, params.getSoundSensitivity());
		intent.putExtra(MOTION_COMPENSATION_EXTRA, params.isMotionCompensation());
		intent.putExtra(MAIL_ALARM_EXTRA, params.isMailOnAlarm());
		intent.putExtra(UPLOAD_ALARM_INTERVAL_EXTRA, params.getUploadOnAlarm());
		intent.putExtra(ENABLE_SCHEDULER_EXTRA, params.isAlarmScheduleEnabled());
		intent.putExtra(MOTION_ENABLED_EXTRA, params.isMotionAlarmEnabled());
		intent.putExtra(SOUND_ENABLED_EXTRA, params.isSoundAlarmEnabled());
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
	
	public void startNotificationSettingsActivity() {
		Intent intent = new Intent(context, NotificationSettingsActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);	
	}
	
	public void snapshot(View v) {
		Intent intent = new Intent(context, SnapshotActivity.class);
		intent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(MainMenuActivity.CAMERA_TYPE_EXTRA, camera.getType());
		startActivity(intent);
	}
	
	public void liveFeed(View v) {
		Intent intent = new Intent(context, LiveFeedActivity.class);
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
	
	public void notifyService() {
		Intent intent = new Intent(this, AlarmService.class);
		startService(intent);
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

}
