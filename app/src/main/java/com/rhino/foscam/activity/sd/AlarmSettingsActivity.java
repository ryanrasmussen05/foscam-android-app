package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.dialog.AlarmScheduleDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.AlarmSchedule;
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.task.sd.AlarmScheduleTask;
import com.rhino.foscam.task.sd.SetAlarmSettingsTask;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class AlarmSettingsActivity extends Activity{
	
	private AlarmSettingsActivity context = this;
	private final Camera camera = new Camera();
	private int motionSensitivity;
	private int soundSensitivity;
	private boolean motionCompensationEnabled;
	private boolean mailOnAlarm;
	private int uploadOnAlarmInterval;
	private boolean schedulerEnabled;
	private boolean motionAlarm;
	private boolean soundAlarm;
	private AlarmScheduleDialog dialog = new AlarmScheduleDialog();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_settings);
		
		Intent intent = getIntent();
		
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		motionSensitivity = intent.getIntExtra(CameraMenuActivity.MOTION_SENSITIVITY_EXTRA, 0);
		soundSensitivity = intent.getIntExtra(CameraMenuActivity.SOUND_SENSITIVITY_EXTRA, 0);
		motionCompensationEnabled = intent.getBooleanExtra(CameraMenuActivity.MOTION_COMPENSATION_EXTRA, false);
		mailOnAlarm = intent.getBooleanExtra(CameraMenuActivity.MAIL_ALARM_EXTRA, false);
		uploadOnAlarmInterval = intent.getIntExtra(CameraMenuActivity.UPLOAD_ALARM_INTERVAL_EXTRA, 0);
		schedulerEnabled = intent.getBooleanExtra(CameraMenuActivity.ENABLE_SCHEDULER_EXTRA, false);
		motionAlarm = intent.getBooleanExtra(CameraMenuActivity.MOTION_ENABLED_EXTRA, false);
		soundAlarm = intent.getBooleanExtra(CameraMenuActivity.SOUND_ENABLED_EXTRA, false);
		
		CheckBox motionAlarmCheckBox = (CheckBox)findViewById(R.id.motionAlarmCheckBox);
		motionAlarmCheckBox.setChecked(motionAlarm);
		
		CheckBox soundAlarmCheckBox = (CheckBox)findViewById(R.id.soundAlarmCheckBox);
		soundAlarmCheckBox.setChecked(soundAlarm);
		
		SeekBar motionSensitivityBar = (SeekBar)findViewById(R.id.motionSensitivity);
		motionSensitivityBar.setMax(9);
		motionSensitivityBar.setProgress(9 - motionSensitivity);
		
		CheckBox motionCompensationCheck = (CheckBox)findViewById(R.id.motionCompensation);
		motionCompensationCheck.setChecked(motionCompensationEnabled);
		
		SeekBar soundSensitivityBar = (SeekBar)findViewById(R.id.soundSensitivity);
		soundSensitivityBar.setMax(9);
		soundSensitivityBar.setProgress(9 - soundSensitivity);
		
		CheckBox mailOnAlarmCheck = (CheckBox)findViewById(R.id.mailAlarm);
		mailOnAlarmCheck.setChecked(mailOnAlarm);
		
		CheckBox uploadOnAlarmCheck = (CheckBox)findViewById(R.id.uploadAlarm);
		uploadOnAlarmCheck.setChecked(uploadOnAlarmInterval > 0);
		
		EditText uploadInterval = (EditText)findViewById(R.id.uploadInterval);
		uploadInterval.setText(String.valueOf(uploadOnAlarmInterval));
		
		if(uploadOnAlarmInterval <= 0) {
			((LinearLayout)findViewById(R.id.uploadIntervalWrapper)).setVisibility(View.GONE);
		}
		
		uploadOnAlarmCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					((LinearLayout)findViewById(R.id.uploadIntervalWrapper)).setVisibility(View.VISIBLE);
				} else {
					((LinearLayout)findViewById(R.id.uploadIntervalWrapper)).setVisibility(View.GONE);
				}
			}
		});
		
		CheckBox schedulerEnabledCheck = (CheckBox)findViewById(R.id.enableScheduler);
		schedulerEnabledCheck.setChecked(schedulerEnabled);
	}
	
	@Override
	public void onBackPressed() {
		if(dialog.isVisible()) {
			FragmentManager fragmentManager = context.getFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
			transaction.remove(dialog).commit();
			dialog.dismiss();
		} else {
			finish();
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		}
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
	
	public void save(View v) {
		SeekBar motionSensitivityBar = (SeekBar)findViewById(R.id.motionSensitivity);	
		CheckBox motionCompensationCheck = (CheckBox)findViewById(R.id.motionCompensation);
		SeekBar soundSensitivityBar = (SeekBar)findViewById(R.id.soundSensitivity);
		CheckBox mailOnAlarmCheck = (CheckBox)findViewById(R.id.mailAlarm);
		CheckBox uploadOnAlarmCheck = (CheckBox)findViewById(R.id.uploadAlarm);
		EditText uploadInterval = (EditText)findViewById(R.id.uploadInterval);
		CheckBox schedulerEnabledCheck = (CheckBox)findViewById(R.id.enableScheduler);
		CheckBox motionAlarmCheckBox = (CheckBox)findViewById(R.id.motionAlarmCheckBox);
		CheckBox soundAlarmCheckBox = (CheckBox)findViewById(R.id.soundAlarmCheckBox);
		
		CameraParams params = new CameraParams();
		params.setMotionSensitivity(9 - motionSensitivityBar.getProgress());
		params.setMotionCompensation(motionCompensationCheck.isChecked());
		params.setSoundSensitivity(9 - soundSensitivityBar.getProgress());
		params.setMailOnAlarm(mailOnAlarmCheck.isChecked());
		params.setMotionAlarmEnabled(motionAlarmCheckBox.isChecked());
		params.setSoundAlarmEnabled(soundAlarmCheckBox.isChecked());
		
		int uploadInt = Integer.parseInt(uploadInterval.getText().toString());
		if(uploadOnAlarmCheck.isChecked() && uploadInt > 0) {
			params.setUploadOnAlarm(uploadInt);
		} else {
			params.setUploadOnAlarm(0);
		}
		
		params.setAlarmScheduleEnabled(schedulerEnabledCheck.isChecked());
		
		SetAlarmSettingsTask task = new SetAlarmSettingsTask(context, camera, params);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void editSchedule(View v) {
		AlarmScheduleTask task = new AlarmScheduleTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void showAlarmSchedule(AlarmSchedule sched) {
		FragmentManager fragmentManager = context.getFragmentManager();
		dialog.setSchedule(sched);
		dialog.setCancelable(false);
		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		transaction.add(android.R.id.content, dialog).addToBackStack(null).commit();
	}
	
	public void closeScheduleDialog() {
		FragmentManager fragmentManager = context.getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		transaction.remove(dialog).commit();
		dialog.dismiss();
	}
	
	public Camera getCamera() {
		return camera;
	}

}
