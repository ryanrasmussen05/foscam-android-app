package com.rhino.foscam;

import com.rhino.foscam.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NotificationSettingsActivity extends Activity{
	
	public static final String NOTIFICATION_PREF = "com.rhino.notificationPrefs";
	public static final String NOTIFICATION_ENABLED = "enableNotification";
	public static final String SNAPSHOT_ENABLED = "snapshotEnabled";
	public static final String NOTIFICATION_SOUND = "notificationSound";
	public static final String SNAPSHOT_COUNT = "snapshotCount";
	public static final int NOTIFICATION_MANAGER_RESPONSE = 6002;
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	private boolean soundChooserOpen = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_settings);
		
		prefs = getSharedPreferences(NOTIFICATION_PREF, Context.MODE_PRIVATE);
		editor = prefs.edit();
		
		boolean notificationsEnabled = prefs.getBoolean(NOTIFICATION_ENABLED, true);
		boolean snapshotsEnabled = prefs.getBoolean(SNAPSHOT_ENABLED, false);
		
		CheckBox notificationCheckbox = (CheckBox)findViewById(R.id.showNotification);
		CheckBox snapshotCheckbox = (CheckBox)findViewById(R.id.getSnapshot);
		
		notificationCheckbox.setChecked(notificationsEnabled);
		snapshotCheckbox.setChecked(snapshotsEnabled);
		
		notificationCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				editor.putBoolean(NOTIFICATION_ENABLED, checked);
				editor.commit();
			}
		});
		
		snapshotCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton view, boolean checked) {
				editor.putBoolean(SNAPSHOT_ENABLED, checked);
				editor.commit();
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
		if(!soundChooserOpen) {
			finish();
		}
	}
	
	public void returnToCameraMenu(View v) {
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void notificationSound(View v) {
		soundChooserOpen = true;
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(prefs.getString(NOTIFICATION_SOUND, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString())));
		startActivityForResult(intent, NOTIFICATION_MANAGER_RESPONSE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(resultCode == Activity.RESULT_OK && requestCode == NOTIFICATION_MANAGER_RESPONSE) {
			soundChooserOpen = false;
			Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			if(uri != null) {
				editor.putString(NOTIFICATION_SOUND, uri.toString());
				editor.commit();
			}
		}
	}
		
}
