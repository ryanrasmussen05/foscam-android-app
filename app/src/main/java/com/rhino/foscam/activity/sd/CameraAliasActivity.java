package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.task.sd.SetAliasTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CameraAliasActivity extends Activity{
	
	private final Camera camera = new Camera();
	private final CameraAliasActivity context = this;
	private String aliasName = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_alias);
		
		Intent intent = getIntent();

		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		aliasName = intent.getStringExtra(CameraMenuActivity.ALIAS_EXTRA);
		
		EditText aliasEditText = (EditText)findViewById(R.id.aliasText);
		aliasEditText.setText(aliasName);
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
	
	public void saveAlias(View v) {
		EditText aliasEditText = (EditText)findViewById(R.id.aliasText);
		String newAlias = aliasEditText.getText().toString();
		
		SetAliasTask task = new SetAliasTask(context, camera, newAlias);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void saveComplete() {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}

}
