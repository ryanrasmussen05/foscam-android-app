package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.task.SaveSnapshotTask;
import com.rhino.foscam.task.sd.SnapshotTask;
import com.rhino.foscam.view.ZoomImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class SnapshotActivity extends Activity{
	
	private final Camera camera = new Camera();
	private SnapshotActivity context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.snapshot);
		
		Intent intent = getIntent();
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		
		SnapshotTask task = new SnapshotTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		finish();
	}
	
	public void failedClose() {
		finish();
	}
	
	public void failedRetry() {
		SnapshotTask task = new SnapshotTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);		
	}
	
	public void returnToCameraMenu(View v) {
		finish();
	}
	
	public void closeSnapshot(View v) {
		finish();
	}
	
	public void saveSnapshot(View v) {
		ZoomImageView snapshotView = (ZoomImageView)findViewById(R.id.snapshot);
		Bitmap snapshot = ((BitmapDrawable)snapshotView.getDrawable()).getBitmap();
		SaveSnapshotTask task = new SaveSnapshotTask(context, snapshot);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void showSnapshot(Bitmap snapshot) {
		ZoomImageView snapshotView = (ZoomImageView)findViewById(R.id.snapshot);
		snapshotView.setBitmap(snapshot);
	}
}
