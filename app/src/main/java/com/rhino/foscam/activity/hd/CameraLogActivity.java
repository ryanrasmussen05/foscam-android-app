package com.rhino.foscam.activity.hd;

import com.rhino.foscam.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class CameraLogActivity extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_log);
		
		Intent intent = getIntent();
		String log = intent.getStringExtra(CameraMenuActivity.LOG_EXTRA);
		
		TextView logText = (TextView)findViewById(R.id.cameraLogText);
		logText.setText(log);
		
		logText.setMovementMethod(new ScrollingMovementMethod());
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

}
