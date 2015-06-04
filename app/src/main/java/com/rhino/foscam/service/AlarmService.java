package com.rhino.foscam.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class AlarmService extends Service {
	
	private Context context = this;
	
	public static final String ALARM_SERVICE_PREF = "com.rhino.alarmService";
	public static final String CAMERA_LIST = "cameraNotificationList";
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	
	private ArrayList<Camera> cameras = new ArrayList<Camera>();
	private HashMap<String, CameraWorker> workerMap = new HashMap<String, CameraWorker>();

	@Override
	public void onCreate() {
		super.onCreate();
		prefs = getApplicationContext().getSharedPreferences(ALARM_SERVICE_PREF, Context.MODE_PRIVATE);
		editor = prefs.edit();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//check for active cameras, stop service if none
		updateCameras();
		
		if(cameras.size() > 0) {
			startWorkers();
			stopWorkers();
		} else {
			stopSelf();
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		stopAllWorkers();
		
		//clear running services from preferences
		editor.putString(CAMERA_LIST, "");
		editor.commit();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void updateCameras() {
		cameras = new ArrayList<Camera>();
		String cameraTempList = prefs.getString(CAMERA_LIST, "");
		String[] camerasTempList = cameraTempList.split(MainMenuActivity.CAMERA_SEPARATOR);
		
		if(camerasTempList[0].length() > 0) {
			for(String tempCamera : camerasTempList) {
				String[] cameraFields = tempCamera.split(MainMenuActivity.FIELD_SEPARATOR);
				
				Camera c = new Camera();
				c.setCameraName(cameraFields[0]);
				c.setCameraUrl(cameraFields[1]);
				c.setPort(cameraFields[2]);
				c.setUsername(cameraFields[3]);
				
				if(cameraFields.length > 4) {
					c.setPassword(cameraFields[4]);
				} else {
					c.setPassword("");
				}
				
				if(cameraFields.length > 5) {
					c.setType(cameraFields[5]);
				} else {
					c.setType("0");
				}
				cameras.add(c);
			}
		}
	}
	
	//add any new cameras in camera list to hash map and start worker threads
	private void startWorkers() {
		for(Camera camera : cameras) {
			if(!workerMap.containsKey(camera.getCameraName())) {
				CameraWorker newCameraWorker = new CameraWorker(context, camera);
				workerMap.put(camera.getCameraName(), newCameraWorker);
			}
		}
	}
	
	//remove any cameras from hash map that were removed from cameras list and stop worker threads
	private void stopWorkers() {
		for(String runningWorker : workerMap.keySet()) {
			
			boolean shouldStop = true;
			for(Camera camera : cameras) {
				if(camera.getCameraName().equals(runningWorker)) {
					shouldStop = false;
					break;
				}
			}
			
			if(shouldStop) {
				workerMap.get(runningWorker).stopThread();
				workerMap.remove(runningWorker);
			}
		}
	}
	
	private void stopAllWorkers() {
		for(CameraWorker worker : workerMap.values()) {
			worker.stopThread();
		}
		workerMap.clear();
	}

}