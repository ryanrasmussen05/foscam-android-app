package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.pojo.Camera;

import android.os.AsyncTask;

public class CameraControlTask extends AsyncTask<Void, Void, Void>{
	
	public Camera camera;
	public int command;
	
	public CameraControlTask(Camera camera, int command) {
		this.camera = camera;
		this.command = command;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			CameraUtilsSD.cameraControl(camera, command);
		} catch (Exception e) {}
		return null;
	}

}
