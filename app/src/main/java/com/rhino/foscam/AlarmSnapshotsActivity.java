package com.rhino.foscam;

import com.rhino.foscam.R;
import com.rhino.foscam.adapter.GalleryAdapter;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.view.ZoomImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

public class AlarmSnapshotsActivity extends Activity{
	
	private final Camera camera = new Camera();
	private Gallery gallery;
	private ZoomImageView snapshot;
	private TextView snapshotTime;
	private GalleryAdapter adapter;
	private AlarmSnapshotsActivity context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_snapshots);
		
		Intent intent = getIntent();
		camera.setCameraName(intent.getStringExtra(MainMenuActivity.CAMERA_NAME_EXTRA));
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		
		gallery = (Gallery)findViewById(R.id.snapshotGallery);
		snapshotTime = (TextView)findViewById(R.id.snapshotTime);
		
		adapter = new GalleryAdapter(context, camera);
		gallery.setAdapter(adapter);
		gallery.setOnItemClickListener(getListener());		
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus) {
			snapshot = (ZoomImageView)findViewById(R.id.snapshot);
			snapshot.setBitmap(adapter.snapshots.get(adapter.snapshots.size() - 1).getBitmap());
			snapshotTime.setText(adapter.snapshots.get(adapter.snapshots.size() - 1).getSnapshotTime());
		}
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
	
	public void returnToCameraMenu(View v) {
		finish();
	}
	
	private OnItemClickListener getListener() {
		OnItemClickListener listener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index, long id) {
				snapshot.setBitmap(adapter.snapshots.get(adapter.snapshots.size() - index - 1).getBitmap());
				snapshotTime.setText(adapter.snapshots.get(adapter.snapshots.size() - index - 1).getSnapshotTime());
			}
		};
		return listener;
	}
}
