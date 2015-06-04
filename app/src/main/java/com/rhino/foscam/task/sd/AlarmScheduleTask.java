package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.AlarmSettingsActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.AlarmSchedule;

import android.os.AsyncTask;
import android.widget.Toast;

public class AlarmScheduleTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public AlarmSettingsActivity context;
	public Camera camera;
	public AlarmSchedule alarmSchedule;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer SUCCESS = 1;
	
	public AlarmScheduleTask(AlarmSettingsActivity context, Camera camera) {
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
		try {
			alarmSchedule = CameraUtilsSD.getAlarmSchedule(camera);
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
            Toast toast = Toast.makeText(context, "Failed to get schedule, try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS) {
            context.showAlarmSchedule(alarmSchedule);
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
