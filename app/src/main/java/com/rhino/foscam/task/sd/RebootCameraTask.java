package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.ManageUsersActivity;
import com.rhino.foscam.activity.sd.PTSettingsActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

public class RebootCameraTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public Activity context;
	public Camera camera;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer FAIL = 1;
	public static final Integer SUCCESS = 2;
	
	public RebootCameraTask(Activity context, Camera camera) {
		this.context = context;
		this.camera = camera;
	}
	
	@Override
	protected void onPreExecute() {
		connectDialog = new ConnectDialog();
		connectDialog.setCancelable(false);
		try {
			connectDialog.show(context.getFragmentManager(), "connect");
		} catch(Exception e){}
	}

	@Override
	protected Integer doInBackground(Void... params) {
		boolean success = false;
		try {
			success = CameraUtilsSD.rebootCamera(camera);
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
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
		
		if(result == CONNECTION_ERROR) {
            Toast toast = Toast.makeText(context, "Failed to reboot, connection failed", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == FAIL) {
            Toast toast = Toast.makeText(context, "Camera rejected reboot, please try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS){
			if(context instanceof ManageUsersActivity) {
				((ManageUsersActivity)context).rebootCamera();
			} else if(context instanceof PTSettingsActivity) {
				((PTSettingsActivity)context).rebootCamera();
			}
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
