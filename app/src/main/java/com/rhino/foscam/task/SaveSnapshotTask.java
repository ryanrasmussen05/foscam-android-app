package com.rhino.foscam.task;

import java.io.File;

import com.rhino.foscam.accessor.StorageUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.widget.Toast;

public class SaveSnapshotTask extends AsyncTask<Void, Void, Integer>{
	
	public Activity context;
	public Bitmap snapshot;
	public File snapshotFile;
	
	public static final Integer FAIL = 1;
	public static final Integer SUCCESS = 2;
	
	public SaveSnapshotTask(Activity context, Bitmap snapshot) {
		this.context = context;
		this.snapshot = snapshot;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		try {
			snapshotFile = StorageUtils.saveSnapshot(snapshot, context);
			String[] paths = new String[1];
			paths[0] = snapshotFile.getCanonicalPath();
			MediaScannerConnection.scanFile(context, paths, null, null);
		} catch (Exception e) {
			return FAIL;
		}
		return SUCCESS;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		if(result == FAIL) {
			Toast toast = Toast.makeText(context, "Failed to save", Toast.LENGTH_LONG);
			toast.show();
		} else if(result == SUCCESS){
			Toast toast = Toast.makeText(context, "Snapshot saved to device", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
}
