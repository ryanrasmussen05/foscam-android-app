package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.MiscParams;
import com.rhino.foscam.task.sd.RebootCameraTask;
import com.rhino.foscam.task.sd.SetPTTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;

public class PTSettingsActivity extends Activity{
	
	private final Camera camera = new Camera();
	private final PTSettingsActivity context = this;
	private boolean disablePreset = false;
	private boolean presetOnBoot = false;
	private boolean centerOnBoot = false;
	private int patrolRoundsVertical = 0;
	private int patrolRoundsHorizontal = 0;
	private int patrolRate = 0;
	private int upwardRate = 0;
	private int downwardRate = 0;
	private int leftwardRate = 0;
	private int rightwardRate = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pt_settings);
		
		Intent intent = getIntent();

		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		
		disablePreset = intent.getBooleanExtra(CameraMenuActivity.PT_DISABLE_PRESET_EXTRA, false);
		presetOnBoot = intent.getBooleanExtra(CameraMenuActivity.PT_PRESET_BOOT_EXTRA, false);
		centerOnBoot = intent.getBooleanExtra(CameraMenuActivity.PT_CENTER_BOOT_EXTRA, false);
		patrolRoundsVertical = intent.getIntExtra(CameraMenuActivity.PT_ROUNDS_VERTICAL_EXTRA, 1);
		patrolRoundsHorizontal = intent.getIntExtra(CameraMenuActivity.PT_ROUNDS_HORIZONTAL_EXTRA, 1);
		patrolRate = intent.getIntExtra(CameraMenuActivity.PT_PATROL_RATE_EXTRA, 0);
		upwardRate = intent.getIntExtra(CameraMenuActivity.PT_UPWARD_EXTRA, 0);
		downwardRate = intent.getIntExtra(CameraMenuActivity.PT_DOWNWARD_EXTRA, 0);
		leftwardRate = intent.getIntExtra(CameraMenuActivity.PT_LEFTWARD_EXTRA, 0);
		rightwardRate = intent.getIntExtra(CameraMenuActivity.PT_RIGHTWARD_EXTRA, 0);
		
		CheckBox presetBoot = (CheckBox)findViewById(R.id.presetBoot);
		presetBoot.setChecked(presetOnBoot);
		CheckBox centerBoot = (CheckBox)findViewById(R.id.centerBoot);
		centerBoot.setChecked(centerOnBoot);
		
		if(disablePreset) {
			findViewById(R.id.centerBoot).setVisibility(View.VISIBLE);
			findViewById(R.id.presetBootWrapper).setVisibility(View.GONE);
		} else {
			findViewById(R.id.centerBoot).setVisibility(View.GONE);
			findViewById(R.id.presetBootWrapper).setVisibility(View.VISIBLE);			
		}
		
		CheckBox disPre = (CheckBox)findViewById(R.id.disablePreset);
		disPre.setChecked(disablePreset);
		disPre.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					findViewById(R.id.centerBoot).setVisibility(View.VISIBLE);
					findViewById(R.id.presetBootWrapper).setVisibility(View.GONE);
				} else {
					findViewById(R.id.centerBoot).setVisibility(View.GONE);
					findViewById(R.id.presetBootWrapper).setVisibility(View.VISIBLE);
				}
			}
		});
		
		((Spinner)findViewById(R.id.patrolRoundsVertical)).setSelection(patrolRoundsVertical);
		((Spinner)findViewById(R.id.patrolRoundsHorizontal)).setSelection(patrolRoundsHorizontal);
		
		SeekBar patrol = (SeekBar)findViewById(R.id.ptSpeed);
		patrol.setMax(15);
		patrol.setProgress(15 - patrolRate/2);
		
		SeekBar upward = (SeekBar)findViewById(R.id.upwardSpeed);
		upward.setMax(15);
		upward.setProgress(15 - upwardRate/2);
		
		SeekBar downward = (SeekBar)findViewById(R.id.downwardSpeed);
		downward.setMax(15);
		downward.setProgress(15 - downwardRate/2);	
		
		SeekBar leftward = (SeekBar)findViewById(R.id.leftwardSpeed);
		leftward.setMax(15);
		leftward.setProgress(15 - leftwardRate/2);
		
		SeekBar rightward = (SeekBar)findViewById(R.id.rightwardSpeed);
		rightward.setMax(15);
		rightward.setProgress(15 - rightwardRate/2);

	}
	
	@Override
	public void onBackPressed() {
		setResult(1);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	@Override
	public void onPause() {
		setResult(1);
		super.onPause();
		finish();
	}
	
	public void returnToCameraMenu(View v) {
		setResult(1);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void save(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Users");
        builder.setMessage("Caution.  Editing P/T Settings will cause the camera to reboot.  You will be logged out and have to wait for the camera to reboot.");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				continueSave();
				dialog.dismiss();
			}
		});
        AlertDialog dialog = builder.create();
        dialog.show();
	}
	
	public void continueSave() {
		SeekBar patrol = (SeekBar)findViewById(R.id.ptSpeed);
		SeekBar upward = (SeekBar)findViewById(R.id.upwardSpeed);
		SeekBar downward = (SeekBar)findViewById(R.id.downwardSpeed);
		SeekBar leftward = (SeekBar)findViewById(R.id.leftwardSpeed);
		SeekBar rightward = (SeekBar)findViewById(R.id.rightwardSpeed);
		Spinner roundsVertical = (Spinner)findViewById(R.id.patrolRoundsVertical);
		Spinner roundsHorizontal = (Spinner)findViewById(R.id.patrolRoundsHorizontal);
		CheckBox disablePreset = (CheckBox)findViewById(R.id.disablePreset);
		CheckBox presetBoot = (CheckBox)findViewById(R.id.presetBoot);
		CheckBox centerBoot = (CheckBox)findViewById(R.id.centerBoot);
		
		int patrolVal = (15 - patrol.getProgress()) * 2;
		int upwardVal = (15 - upward.getProgress()) * 2;
		int downwardVal = (15 - downward.getProgress()) * 2;
		int leftwardVal = (15 - leftward.getProgress()) * 2;
		int rightwardVal = (15 - rightward.getProgress()) * 2;
		int roundsVerticalVal = roundsVertical.getSelectedItemPosition();
		int roundsHorizontalVal = roundsHorizontal.getSelectedItemPosition();
		
		boolean presetBootVal, centerBootVal;
		boolean disablePresetVal = disablePreset.isChecked();
		
		if(disablePresetVal) {
			presetBootVal = false;
			centerBootVal = centerBoot.isChecked();
		} else {
			presetBootVal = presetBoot.isChecked();
			centerBootVal = false;
		}
		
		MiscParams misc = new MiscParams();
		misc.setDisablePreset(disablePresetVal);
		misc.setPresetOnBoot(presetBootVal);
		misc.setCenterOnBoot(centerBootVal);
		misc.setPatrolRate(patrolVal);
		misc.setUpwardRate(upwardVal);
		misc.setDownwardRate(downwardVal);
		misc.setRightwaredRate(rightwardVal);
		misc.setLeftwardRate(leftwardVal);
		misc.setPatrolRoundsVertical(roundsVerticalVal);
		misc.setPatrolRoundsHorizontal(roundsHorizontalVal);
		
		SetPTTask task = new SetPTTask(context, camera, misc);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void saveComplete() {
		RebootCameraTask task = new RebootCameraTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
		
	public void rebootCamera() {
		setResult(0);
		finish();
	}
	
}
