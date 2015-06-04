package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.LiveFeedActivity;
import com.rhino.foscam.pojo.Camera;

import android.os.AsyncTask;

public class IRControlTask extends AsyncTask<Void, Void, Integer>{
	
	public static final Integer FAIL = 1;
	public static final Integer SUCCESS = 2;
	
	public Camera camera;
	public int command;
	public LiveFeedActivity context;
	
	public IRControlTask(LiveFeedActivity context, Camera camera, int command) {
		this.camera = camera;
		this.command = command;
		this.context = context;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		boolean success = false;
		try {
			success = CameraUtilsSD.cameraControl(camera, command);
		} catch (Exception e) {
			return FAIL;
		}
		if(success) {
			return SUCCESS;
		} else {
			return FAIL;
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		if(result == FAIL) {
            context.IRUpdate(true, false);
		} else if(command == CameraUtilsSD.COMMAND_IR_ON){
			context.IRUpdate(true, true);
		} else {
			context.IRUpdate(false, true);
		}
	}

}