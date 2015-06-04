package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.CameraAliasActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;

import android.os.AsyncTask;
import android.widget.Toast;

public class SetAliasTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public CameraAliasActivity context;
	public Camera camera;
	public String newAlias;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer SUCCESS = 1;
	public static final Integer FAIL = 2;
	
	public SetAliasTask(CameraAliasActivity context, Camera camera, String newAlias) {
		this.context = context;
		this.camera = camera;
		this.newAlias = newAlias;
	}
	
	@Override
	protected void onPreExecute() {
		connectDialog = new ConnectDialog();
		connectDialog.setCancelable(false);
		connectDialog.show(context.getFragmentManager(), "connect");
	}

	@Override
	protected Integer doInBackground(Void... params) {
		boolean success = false;
		try {
			success = CameraUtilsSD.setAlias(camera, newAlias);
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
            Toast toast = Toast.makeText(context, "Failed to set alias, connection failed", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == FAIL) {
            Toast toast = Toast.makeText(context, "Camera rejected alias, please try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS){
			Toast toast = Toast.makeText(context, "Alias update successful", Toast.LENGTH_SHORT);
			toast.show();
			context.saveComplete();
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
