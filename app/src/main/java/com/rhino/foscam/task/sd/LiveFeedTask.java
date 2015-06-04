package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.accessor.MjpegInputStream;
import com.rhino.foscam.activity.sd.LiveFeedActivity;
import com.rhino.foscam.dialog.ConnectDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.VideoParams;
import com.rhino.foscam.view.ZoomImageView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class LiveFeedTask extends AsyncTask<Void, VideoParams, Integer>{
	
	private ConnectDialog connectDialog;
	private LiveFeedActivity context;
	private Camera camera;
	private ZoomImageView videoView;
	private MjpegInputStream stream;
	private Bitmap latestImage;
	private boolean initialImage = true;
	public VideoParams videoParams;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer CONNECTION_LOST = 1;
	public static final Integer SUCCESS = 2;
	public static final Integer CANCELLED = 3;
	
	public LiveFeedTask(LiveFeedActivity context, Camera camera, ZoomImageView videoView, MjpegInputStream stream) {
		this.context = context;
		this.camera = camera;
		this.videoView = videoView;
		this.stream = stream;
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
			videoParams = CameraUtilsSD.getVideoParams(camera);
		} catch (Exception e) {
			connectDialog.dismiss();
			return CONNECTION_ERROR;
		}
		
		publishProgress(videoParams);
		
		try {
			if(camera.isHD()) {
				//TODO
			} else {
				stream = CameraUtilsSD.videoStream(camera);
			}
		} catch (Exception e) {
			connectDialog.dismiss();
			return CONNECTION_ERROR;
		}
		
		connectDialog.dismiss();
		
		while(true) {
			try {
				if(isCancelled()) {
					return CANCELLED;
				}
				latestImage = stream.readMjpegFrame();
			} catch (Exception e) {
				return CONNECTION_LOST;
			}
			
			publishProgress();
		}
	}
	
	@Override
	protected void onProgressUpdate(VideoParams... params) {
		if(params.length > 0) {
			context.setVideoParams(videoParams);
		} else {
			if(initialImage) {
				videoView.setBitmap(latestImage);
				initialImage = false;
			} else {
				videoView.updateBitmap(latestImage);
			}
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			stream.close();
		} catch (Exception e) {}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		try {
			stream.close();
		} catch (Exception e) {}
		
		connectDialog.dismiss();
		
		if(result == CONNECTION_ERROR) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Video Failed");
            builder.setMessage("Failed to get video feed.  Please verify that you have an internet connection and the camera is properly configured.");
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
		} else if (result == CONNECTION_LOST) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Connection Lost");
            builder.setMessage("Connection to the camera was lost.  Please verify that you have a stable internet connection and try again");
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
		}
	}
}