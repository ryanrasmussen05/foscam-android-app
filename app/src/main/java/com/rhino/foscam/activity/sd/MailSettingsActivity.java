package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.task.sd.SetMailTask;
import com.rhino.foscam.task.sd.TestMailTask;

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

public class MailSettingsActivity extends Activity{
	
	private final Camera camera = new Camera();
	private final MailSettingsActivity context = this;
	private String mailServer = "";
	private String mailPort = "";
	private String mailUser = "";
	private String mailPassword = "";
	private String mailSender = "";
	private String mailReceiver1 = "";
	private String mailReceiver2 = "";
	private String mailReceiver3 = "";
	private String mailReceiver4 = "";
	private int mailMode = 0;
	private boolean mailReportIP = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mail_settings);
		
		Intent intent = getIntent();

		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		mailServer = intent.getStringExtra(CameraMenuActivity.MAIL_SERVER_EXTRA);
		mailPort = intent.getStringExtra(CameraMenuActivity.MAIL_PORT_EXTRA);
		mailUser = intent.getStringExtra(CameraMenuActivity.MAIL_USER_EXTRA);
		mailPassword = intent.getStringExtra(CameraMenuActivity.MAIL_PASSWORD_EXTRA);
		mailSender = intent.getStringExtra(CameraMenuActivity.MAIL_SENDER_EXTRA);
		mailReceiver1 = intent.getStringExtra(CameraMenuActivity.MAIL_RECEIVER_1_EXTRA);
		mailReceiver2 = intent.getStringExtra(CameraMenuActivity.MAIL_RECEIVER_2_EXTRA);
		mailReceiver3 = intent.getStringExtra(CameraMenuActivity.MAIL_RECEIVER_3_EXTRA);
		mailReceiver4 = intent.getStringExtra(CameraMenuActivity.MAIL_RECEIVER_4_EXTRA);
		mailMode = intent.getIntExtra(CameraMenuActivity.MAIL_MODE_EXTRA, 0);
		mailReportIP = intent.getBooleanExtra(CameraMenuActivity.MAIL_REPORT_IP_EXTRA, false);

		
		((EditText)findViewById(R.id.mailServer)).setText(mailServer);
		((EditText)findViewById(R.id.mailPort)).setText(mailPort);
		((EditText)findViewById(R.id.mailUser)).setText(mailUser);
		((EditText)findViewById(R.id.mailPassword)).setText(mailPassword);
		((EditText)findViewById(R.id.mailSender)).setText(mailSender);
		((EditText)findViewById(R.id.mailReceiver1)).setText(mailReceiver1);
		((EditText)findViewById(R.id.mailReceiver2)).setText(mailReceiver2);
		((EditText)findViewById(R.id.mailReceiver3)).setText(mailReceiver3);
		((EditText)findViewById(R.id.mailReceiver4)).setText(mailReceiver4);
		((Spinner)findViewById(R.id.mailMode)).setSelection(mailMode);
		
		CheckBox authenticate = (CheckBox)findViewById(R.id.mailAuthentication);
		
		if(mailUser.isEmpty()) {
			authenticate.setChecked(false);
			findViewById(R.id.mailUserWrapper).setVisibility(View.GONE);
			findViewById(R.id.mailPasswordWrapper).setVisibility(View.GONE);
		} else {
			authenticate.setChecked(true);
		}
		
		authenticate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				LinearLayout wrapper1 = (LinearLayout)findViewById(R.id.mailUserWrapper);
				LinearLayout wrapper2 = (LinearLayout)findViewById(R.id.mailPasswordWrapper);
				if(isChecked) {
					wrapper1.setVisibility(View.VISIBLE);
					wrapper2.setVisibility(View.VISIBLE);
				} else {
					wrapper1.setVisibility(View.GONE);
					wrapper2.setVisibility(View.GONE);
				}
			}
		});
		
		CheckBox reportIP = (CheckBox)findViewById(R.id.mailReportIP);
		reportIP.setChecked(mailReportIP);
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
		TestMailTask task = new TestMailTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void save(View v) {
		CameraParams params = new CameraParams();
		params.setMailServer(((EditText)findViewById(R.id.mailServer)).getText().toString());
		params.setMailPort(((EditText)findViewById(R.id.mailPort)).getText().toString());
		params.setMailSender(((EditText)findViewById(R.id.mailSender)).getText().toString());
		params.setMailReceiver1(((EditText)findViewById(R.id.mailReceiver1)).getText().toString());
		params.setMailReceiver2(((EditText)findViewById(R.id.mailReceiver2)).getText().toString());
		params.setMailReceiver3(((EditText)findViewById(R.id.mailReceiver3)).getText().toString());
		params.setMailReceiver4(((EditText)findViewById(R.id.mailReceiver4)).getText().toString());
		params.setMailMode(((Spinner)findViewById(R.id.mailMode)).getSelectedItemPosition());
		params.setMailReportIP(((CheckBox)findViewById(R.id.mailReportIP)).isChecked());
		if(((CheckBox)findViewById(R.id.mailAuthentication)).isChecked()) {
			params.setMailUser(((EditText)findViewById(R.id.mailUser)).getText().toString());
			params.setMailPassword(((EditText)findViewById(R.id.mailPassword)).getText().toString());
		} else {
			params.setMailUser("");
			params.setMailPassword("");
		}
		
		SetMailTask task = new SetMailTask(context, camera, params, false);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void saveTest(View v) {
		CameraParams params = new CameraParams();
		params.setMailServer(((EditText)findViewById(R.id.mailServer)).getText().toString());
		params.setMailPort(((EditText)findViewById(R.id.mailPort)).getText().toString());
		params.setMailSender(((EditText)findViewById(R.id.mailSender)).getText().toString());
		params.setMailReceiver1(((EditText)findViewById(R.id.mailReceiver1)).getText().toString());
		params.setMailReceiver2(((EditText)findViewById(R.id.mailReceiver2)).getText().toString());
		params.setMailReceiver3(((EditText)findViewById(R.id.mailReceiver3)).getText().toString());
		params.setMailReceiver4(((EditText)findViewById(R.id.mailReceiver4)).getText().toString());
		params.setMailMode(((Spinner)findViewById(R.id.mailMode)).getSelectedItemPosition());
		params.setMailReportIP(((CheckBox)findViewById(R.id.mailReportIP)).isChecked());
		if(((CheckBox)findViewById(R.id.mailAuthentication)).isChecked()) {
			params.setMailUser(((EditText)findViewById(R.id.mailUser)).getText().toString());
			params.setMailPassword(((EditText)findViewById(R.id.mailPassword)).getText().toString());
		} else {
			params.setMailUser("");
			params.setMailPassword("");
		}
		
		SetMailTask task = new SetMailTask(context, camera, params, true);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
	}
	

}
