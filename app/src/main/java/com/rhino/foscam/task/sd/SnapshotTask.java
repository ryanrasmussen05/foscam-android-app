package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.SnapshotActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class SnapshotTask extends AsyncTask<Void, Void, Integer>{
	
	public ConnectDialog connectDialog;
	public SnapshotActivity context;
	public Camera camera;
	public Bitmap snapshot;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer SUCCESS = 1;
	
	public SnapshotTask(SnapshotActivity context, Camera camera) {
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
			snapshot = CameraUtilsSD.snapshot(camera);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Snapshot Failed");
            builder.setMessage("Failed to get snapshot.  Please verify that you have an internet connection and the camera is properly configured.");
            builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					context.failedClose();
				}
			});
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					context.failedRetry();
				}
			});
            AlertDialog failDialog = builder.create();
            failDialog.show();
		} else {
			context.showSnapshot(snapshot);
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			connectDialog.dismiss();
		} catch(Exception e){}
	}
}
