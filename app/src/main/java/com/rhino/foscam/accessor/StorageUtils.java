package com.rhino.foscam.accessor;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.rhino.foscam.exception.GeneralException;
import com.rhino.foscam.pojo.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;

public class StorageUtils {
	
	public static final String PICTURE_DIR = "Foscam";
	
	public static File saveSnapshot(Bitmap snapshot, Activity context) throws Exception {
		if(!isExternalStorageWritable()) {
			throw new GeneralException();
		}
		
		File directory = new File(Environment.getExternalStorageDirectory(), PICTURE_DIR);
		directory.mkdirs();
		
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yy_HH:mm:ss");
		Date date = new Date();
		String formattedDate = dateFormat.format(date);
		
		String snapshotFilename = "Snapshot_" + formattedDate + ".jpg";
		File snapshotFile = new File(directory, snapshotFilename);
		FileOutputStream out = new FileOutputStream(snapshotFile);
		
		try {
			snapshot.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();
		}
		
		return snapshotFile;
	}
	
	public static void saveAlarmSnapshot(Bitmap snapshot, Camera camera, Context context) throws Exception {
		if(!isExternalStorageWritable()) {
			throw new GeneralException();
		}
		
		String dir = PICTURE_DIR + File.separator + camera.getCameraName();
		
		File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
		directory.mkdirs();
		
		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yy_HH:mm:ss");
		Date date = new Date();
		String formattedDate = dateFormat.format(date);
		
		String snapshotFilename = formattedDate + ".jpg";
		File snapshotFile = new File(directory, snapshotFilename);
		FileOutputStream out = new FileOutputStream(snapshotFile);
		
		try {
			snapshot.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			out.close();
		}
		
		String[] paths = new String[1];
		paths[0] = snapshotFile.getCanonicalPath();
		MediaScannerConnection.scanFile(context, paths, null, null);
	}

	private static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();		
		return Environment.MEDIA_MOUNTED.equals(state);
	}
	
}