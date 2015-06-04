package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.LiveFeedActivity;
import com.rhino.foscam.dialog.UpdateSettingsDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.VideoParams;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

public class UpdateVideoSettingsTask extends AsyncTask<Void, Void, Integer>{
	
	public UpdateSettingsDialog dialog;
	public LiveFeedActivity context;
	public Camera camera;
	public VideoParams videoParams;
	
	public static final Integer FAIL = 1;
	public static final Integer SUCCESS = 2;
	
	public UpdateVideoSettingsTask(LiveFeedActivity context, Camera camera, VideoParams videoParams) {
		this.context = context;
		this.camera = camera;
		this.videoParams = videoParams;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = new UpdateSettingsDialog();
		dialog.setCancelable(false);
		try {
			dialog.show(context.getFragmentManager(), "connect");
		} catch(Exception e){}
	}

	@Override
	protected Integer doInBackground(Void... params) {
		try {
			if(videoParams.getURLResolution() >= 0) {
				CameraUtilsSD.setVideoResolution(camera, videoParams);
			}
			if(videoParams.getURLBrightness() >= 0) {
				CameraUtilsSD.setVideoBrightness(camera, videoParams);
			}
			if(videoParams.getContrast() >= 0) {
				CameraUtilsSD.setVideoContrast(camera, videoParams);
			}
			if(videoParams.getMode() >= 0) {
				CameraUtilsSD.setVideoMode(camera, videoParams);
			}
			if(videoParams.getFlip() >= 0) {
				CameraUtilsSD.setVideoFlip(camera, videoParams);
			}
		} catch (Exception e) {
			return FAIL;
		}
		return SUCCESS;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		try {
			dialog.dismiss();
		} catch(Exception e){}
		
		if(result == FAIL) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Update Failed");
            builder.setMessage("Failed to update Settings.  Please verify that you have an internet connection and the camera is properly configured.");
            builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					context.failedClose();
				}
			});
            builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					context.failedRetry();
				}
			});
            AlertDialog failDialog = builder.create();
            failDialog.show();
		} else if(result == SUCCESS){
			context.restartVideo();
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			dialog.dismiss();
		} catch(Exception e){}
	}
}
