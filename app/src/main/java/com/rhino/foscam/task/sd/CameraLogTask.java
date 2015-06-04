package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.CameraMenuActivity;
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
	public static final String SEPARATOR = "-----";
	
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
			log = CameraUtilsSD.getCameraLog(camera);
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
			log = log.replace("var log_text=", "");
			log = log.substring(1, log.length() - 2);
			log = log.replace("\\n", SEPARATOR);
			log = log.replaceAll(" +", " ");
			
			String[] logs = log.split(SEPARATOR);
			String newLog = "";
			for(int i = logs.length; i > 0; i--) {
				newLog = newLog + logs[i -1] + System.getProperty("line.separator");
			}
			log = newLog;
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
