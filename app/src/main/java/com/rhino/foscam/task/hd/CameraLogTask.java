package com.rhino.foscam.task.hd;

import com.rhino.foscam.accessor.CameraUtilsHD;
import com.rhino.foscam.activity.hd.CameraMenuActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;

import android.os.AsyncTask;
import android.widget.Toast;

public class CameraLogTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public CameraMenuActivity context;
	public Camera camera;
	public String log;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer SUCCESS = 1;
	
	public CameraLogTask(CameraMenuActivity context, Camera camera) {
		this.context = context;
		this.camera = camera;
	}
	
	@Override
	protected void onPreExecute() {
		connectDialog = new ConnectDialog();
		connectDialog.setCancelable(false);
		connectDialog.show(context.getFragmentManager(), "connect");
	}

	@Override
	protected Integer doInBackground(Void... params) {
		try {
			log = CameraUtilsHD.getCameraLog(camera);
		} catch (Exception e) {
			return CONNECTION_ERROR;
		}
		return SUCCESS;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
		
		if(result == CONNECTION_ERROR) {
            Toast toast = Toast.makeText(context, "Failed to get log, try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS) {
            context.startCameraLog(log);
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
