package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.CameraMenuActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.CameraParams;

import android.os.AsyncTask;
import android.widget.Toast;

public class MailSettingsTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public CameraMenuActivity context;
	public Camera camera;
	public CameraParams cameraParams;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer SUCCESS = 1;
	
	public MailSettingsTask(CameraMenuActivity context, Camera camera) {
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
			cameraParams = CameraUtilsSD.getCameraParams(camera);
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
            Toast toast = Toast.makeText(context, "Failed to get settings, try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS) {
            context.startMailSettingsActivity(cameraParams);
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
