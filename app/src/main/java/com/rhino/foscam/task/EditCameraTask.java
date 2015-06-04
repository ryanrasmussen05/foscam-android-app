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

public class EditCameraTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public MainMenuActivity context;
	public CameraParams cameraParams;
	public DevInfo devInfo;
	public String oldName;
	public String name;
	public String url;
	public String port;
	public String user;
	public String password;
	public String type;
	public Camera camera;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer UNAUTHORIZED = 1;
	public static final Integer SUCCESS = 2;
	
	public EditCameraTask(MainMenuActivity context, String oldName, String name, String url, String port, String user, String password, String type) {
		this.context = context;
		this.oldName = oldName;
		this.name = name;
		this.url = url;
		this.port = port;
		this.user = user;
		this.password = password;
		this.type = type;
		
		this.camera = new Camera();
		camera.setCameraUrl(url);
		camera.setPort(port);
		camera.setUsername(user);
		camera.setPassword(password);
		camera.setType(type);
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
			context.editCamera(oldName, name, url, port, user, password, type);
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
					context.editCamera(oldName, name, url, port, user, password, type);
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
