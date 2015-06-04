package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.AlarmSettingsActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.AlarmSchedule;

import android.os.AsyncTask;
import android.widget.Toast;

public class SetScheduleTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public AlarmSettingsActivity context;
	public Camera camera;
	public AlarmSchedule schedule;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer FAIL = 1;
	public static final Integer SUCCESS = 2;
	
	public SetScheduleTask(AlarmSettingsActivity context, Camera camera, AlarmSchedule schedule) {
		this.context = context;
		this.camera = camera;
		this.schedule = schedule;
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
			success = CameraUtilsSD.setAlarmSchedule(camera, schedule);
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
            Toast toast = Toast.makeText(context, "Connection failed, please try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == FAIL) {
            Toast toast = Toast.makeText(context, "Set alarm schedule failed, please try again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS){
			Toast toast = Toast.makeText(context, "Schedule saved", Toast.LENGTH_SHORT);
			toast.show();
			context.closeScheduleDialog();
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
