package com.rhino.foscam.task;

import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.accessor.CameraUtilsHD;
import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.exception.UnauthorizedException;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.hd.DevInfo;
import com.rhino.foscam.pojo.sd.CameraParams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class AddCameraTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public MainMenuActivity context;
	public Camera camera;
	public CameraParams cameraParams;
	public DevInfo devInfo;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer UNAUTHORIZED = 1;
	public static final Integer SUCCESS = 2;
	
	public AddCameraTask(MainMenuActivity context, String name, String url, String port, String user, String password, String type) {
		this.context = context;
		Camera newCamera = new Camera();
		newCamera.setCameraName(name);
		newCamera.setCameraUrl(url);
		newCamera.setPort(port);
		newCamera.setUsername(user);
		newCamera.setPassword(password);
		newCamera.setType(type);
		this.camera = newCamera;
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
			if(camera.isHD()) {
				devInfo = CameraUtilsHD.getDevInfo(camera);
			} else {
				cameraParams = CameraUtilsSD.getCameraParams(camera);
			}
		} catch (UnauthorizedException e) {
			return UNAUTHORIZED;
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
		
		if(result == SUCCESS) {
			context.addCamera(camera, "Camera Found!");
		} else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Connection Failed");
            builder.setMessage("Make sure the camera settings are correct and the network is configured properly");
            builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
            builder.setPositiveButton("Save Anyway", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					context.addCamera(camera, "Camera Saved");
				}
			});
            builder.create().show();
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
