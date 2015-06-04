package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.LiveFeedActivity;
import com.rhino.foscam.pojo.Camera;

import android.os.AsyncTask;
import android.widget.Toast;

public class SetPresetTask extends AsyncTask<Void, Void, Integer>{
	
	public LiveFeedActivity context;
	public Camera camera;
	public int command;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer FAIL = 1;
	public static final Integer SUCCESS = 2;
	
	public SetPresetTask(LiveFeedActivity context, Camera camera, int command) {
		this.context = context;
		this.camera = camera;
		this.command = command;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		boolean success = false;
		try {
			success = CameraUtilsSD.cameraControl(camera, command);
		} catch (Exception e) {
			return CONNECTION_ERROR;
		}
		if(success) {
			return SUCCESS;
		} else {
			return FAIL;
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		
		if(result == SUCCESS) {
            Toast toast = Toast.makeText(context, "Preset Saved", Toast.LENGTH_LONG);
            toast.show();
		} else {
            Toast toast = Toast.makeText(context, "Failed to save preset, try again", Toast.LENGTH_LONG);
            toast.show();			
		}
	}
}
