package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.task.sd.SetFTPTask;
import com.rhino.foscam.task.sd.TestFTPTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class FTPSettingsActivity extends Activity{
	
	private final Camera camera = new Camera();
	private final FTPSettingsActivity context = this;
	private String ftpServer = "";
	private String ftpPort = "";
	private String ftpUser = "";
	private String ftpPassword = "";
	private String ftpFolder = "";
	private int ftpMode = 0;
	private String ftpInterval = "0";
	private String ftpFilename = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ftp_settings);
		
		Intent intent = getIntent();

		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		ftpServer = intent.getStringExtra(CameraMenuActivity.FTP_SERVER_EXTRA);
		ftpPort = intent.getStringExtra(CameraMenuActivity.FTP_PORT_EXTRA);
		ftpUser = intent.getStringExtra(CameraMenuActivity.FTP_USER_EXTRA);
		ftpPassword = intent.getStringExtra(CameraMenuActivity.FTP_PASSWORD_EXTRA);
		ftpFolder = intent.getStringExtra(CameraMenuActivity.FTP_FOLDER_EXTRA);
		ftpMode = intent.getIntExtra(CameraMenuActivity.FTP_MODE_EXTRA, 0);
		ftpInterval = intent.getStringExtra(CameraMenuActivity.FTP_INTERVAL_EXTRA);
		ftpFilename = intent.getStringExtra(CameraMenuActivity.FTP_FILENAME_EXTRA);
		
		((EditText)findViewById(R.id.ftpServer)).setText(ftpServer);
		((EditText)findViewById(R.id.ftpPort)).setText(ftpPort);
		((EditText)findViewById(R.id.ftpUser)).setText(ftpUser);
		((EditText)findViewById(R.id.ftpPassword)).setText(ftpPassword);
		((EditText)findViewById(R.id.ftpFolder)).setText(ftpFolder);
		((EditText)findViewById(R.id.ftpUploadInterval)).setText(ftpInterval);
		((EditText)findViewById(R.id.ftpFilename)).setText(ftpFilename);
		((Spinner)findViewById(R.id.ftpMode)).setSelection(ftpMode);
		
		CheckBox upload = (CheckBox)findViewById(R.id.ftpUploadNow);
		
		if(ftpInterval.equals("0")) {
			upload.setChecked(false);
			findViewById(R.id.ftpUploadIntervalWrapper).setVisibility(View.GONE);
		} else {
			upload.setChecked(true);
		}
		
		upload.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LinearLayout wrapper = (LinearLayout)findViewById(R.id.ftpUploadIntervalWrapper);
				if(isChecked) {
					wrapper.setVisibility(View.VISIBLE);
				} else {
					wrapper.setVisibility(View.GONE);
					((EditText)findViewById(R.id.ftpUploadInterval)).setText("");
				}
			}
		});
		
		CheckBox setFilename = (CheckBox)findViewById(R.id.ftpSetFilename);
		
		if(ftpFilename.isEmpty()) {
			setFilename.setChecked(false);
			findViewById(R.id.ftpFilenameWrapper).setVisibility(View.GONE);
		} else {
			setFilename.setChecked(true);
		}
		
		setFilename.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LinearLayout wrapper = (LinearLayout)findViewById(R.id.ftpFilenameWrapper);
				if(isChecked) {
					wrapper.setVisibility(View.VISIBLE);
				} else {
					wrapper.setVisibility(View.GONE);
					((EditText)findViewById(R.id.ftpFilename)).setText("");
				}
			}
		});
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
	
	public void beginTest() {
		TestFTPTask task = new TestFTPTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void save(View v) {
		CameraParams params = new CameraParams();
		params.setFtpServer(((EditText)findViewById(R.id.ftpServer)).getText().toString());
		params.setFtpPort(((EditText)findViewById(R.id.ftpPort)).getText().toString());
		params.setFtpUser(((EditText)findViewById(R.id.ftpUser)).getText().toString());
		params.setFtpPassword(((EditText)findViewById(R.id.ftpPassword)).getText().toString());
		params.setFtpFolder(((EditText)findViewById(R.id.ftpFolder)).getText().toString());
		params.setFtpUploadInterval(((EditText)findViewById(R.id.ftpUploadInterval)).getText().toString());
		params.setFtpFilename(((EditText)findViewById(R.id.ftpFilename)).getText().toString());
		params.setFtpMode(((Spinner)findViewById(R.id.ftpMode)).getSelectedItemPosition());
		
		SetFTPTask task = new SetFTPTask(context, camera, params, false);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void saveTest(View v) {
		CameraParams params = new CameraParams();
		params.setFtpServer(((EditText)findViewById(R.id.ftpServer)).getText().toString());
		params.setFtpPort(((EditText)findViewById(R.id.ftpPort)).getText().toString());
		params.setFtpUser(((EditText)findViewById(R.id.ftpUser)).getText().toString());
		params.setFtpPassword(((EditText)findViewById(R.id.ftpPassword)).getText().toString());
		params.setFtpFolder(((EditText)findViewById(R.id.ftpFolder)).getText().toString());
		params.setFtpUploadInterval(((EditText)findViewById(R.id.ftpUploadInterval)).getText().toString());
		params.setFtpFilename(((EditText)findViewById(R.id.ftpFilename)).getText().toString());
		params.setFtpMode(((Spinner)findViewById(R.id.ftpMode)).getSelectedItemPosition());
		
		SetFTPTask task = new SetFTPTask(context, camera, params, true);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);		
	}
	

}
