package com.rhino.foscam.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.NotificationSettingsActivity;
import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.accessor.StorageUtils;
import com.rhino.foscam.activity.sd.CameraMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.CameraStatus;

public class CameraWorker {
	
	private Context context;
	private Camera camera;
	private boolean run = true;
	private final Thread worker = createWorkerThread();
	private SharedPreferences notificationPrefs;
	private long[] vibrationPattern = {0, 100, 200, 100, 200, 100};
	private NotificationCompat.Builder builder;
	
	public CameraWorker(Context context, Camera camera) {
		this.camera = camera;
		this.context = context;
		notificationPrefs = context.getSharedPreferences(NotificationSettingsActivity.NOTIFICATION_PREF, Context.MODE_PRIVATE);
		builder = new NotificationCompat.Builder(context);
		
		startThread();
	}
			
	private Thread createWorkerThread() {
		Thread tempThread = new Thread(new Runnable() {
			public void run() {
				while(run) {
					try {
						doWork();
						Thread.sleep(10000);
					} catch (InterruptedException e) {}
				}
			}
		});
		return tempThread;
	}
	
	private void doWork() throws InterruptedException {
		boolean alarm = false;

		try {
			if(camera.isHD()) {
				//TODO
			} else {
				CameraStatus status = CameraUtilsSD.getCameraStatus(camera);
				if(status.isAlarmActive()) {
					alarm = true;
				}
			}
		} catch (Exception e){}
		
		if (alarm) {
			boolean showNotification = notificationPrefs.getBoolean(NotificationSettingsActivity.NOTIFICATION_ENABLED, true);
			boolean saveSnapshot = notificationPrefs.getBoolean(NotificationSettingsActivity.SNAPSHOT_ENABLED, false);
			Uri soundUri = Uri.parse(notificationPrefs.getString(NotificationSettingsActivity.NOTIFICATION_SOUND, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()));
			
			if(showNotification) {
				builder.setSmallIcon(R.drawable.launch_icon);
				builder.setContentTitle("Alarm Triggered");
				builder.setContentText(camera.getCameraName());
				builder.setSound(soundUri);
				builder.setVibrate(vibrationPattern);
				
				Intent resultIntent = new Intent(context, CameraMenuActivity.class);
				resultIntent.putExtra(MainMenuActivity.CAMERA_NAME_EXTRA, camera.getCameraName());
				resultIntent.putExtra(MainMenuActivity.CAMERA_URL_EXTRA, camera.getCameraUrl());
				resultIntent.putExtra(MainMenuActivity.CAMERA_PORT_EXTRA, camera.getPort());
				resultIntent.putExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA, camera.getUsername());
				resultIntent.putExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA, camera.getPassword());
				
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
				stackBuilder.addParentStack(MainMenuActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.setContentIntent(resultPendingIntent);
				builder.setOnlyAlertOnce(true);

				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				notificationManager.notify(camera.getCameraName().hashCode(), builder.build());
			}
			
			if(saveSnapshot) {
				try {
					if(camera.isHD()) {
						//TODO
					} else {
						Bitmap snapshot = CameraUtilsSD.snapshot(camera);
						StorageUtils.saveAlarmSnapshot(snapshot, camera, context);
						incrementSnapshotCount();
					}
				} catch (Exception e){}
			}
			
			if(!saveSnapshot) {
				Thread.sleep(50000);
			}
		}
	}
	
	private void startThread() {
		run = true;
		worker.start();
	}
	
	public void stopThread() {
		run = false;
		worker.interrupt();
	}
	
	public int getSnapshotCount() {
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
	
	public void incrementSnapshotCount() {
		int count = getSnapshotCount() + 1;
		
		String oldSnapshotCountString = notificationPrefs.getString(NotificationSettingsActivity.SNAPSHOT_COUNT, "");
		String newSnapshotCountString = "";
		String[] snapshotCounts = oldSnapshotCountString.split(MainMenuActivity.CAMERA_SEPARATOR);
		
		boolean notFound = true;
		
		for(String snapshotCount : snapshotCounts) {
			String[] temp = snapshotCount.split(MainMenuActivity.FIELD_SEPARATOR);
			if(camera.getCameraName().equals(temp[0])) {
				String updatedCount = camera.getCameraName() + MainMenuActivity.FIELD_SEPARATOR + count + MainMenuActivity.CAMERA_SEPARATOR;
				newSnapshotCountString = newSnapshotCountString + updatedCount;
				notFound = false;
			} else {
				newSnapshotCountString = newSnapshotCountString + snapshotCount + MainMenuActivity.CAMERA_SEPARATOR;
			}
		}
		
		if(notFound) {
			String updatedCount = camera.getCameraName() + MainMenuActivity.FIELD_SEPARATOR + count + MainMenuActivity.CAMERA_SEPARATOR;
			newSnapshotCountString = newSnapshotCountString + updatedCount;			
		}

		SharedPreferences.Editor editor = notificationPrefs.edit();
		editor.putString(NotificationSettingsActivity.SNAPSHOT_COUNT, newSnapshotCountString);
		editor.commit();
	}

}
