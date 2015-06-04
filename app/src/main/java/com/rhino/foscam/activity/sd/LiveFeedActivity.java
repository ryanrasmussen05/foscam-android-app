package com.rhino.foscam.activity.sd;

import com.ipc.sdk.AVStreamData;
import com.ipc.sdk.FSApi;
import com.ipc.sdk.StatusListener;
import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.accessor.MjpegInputStream;
import com.rhino.foscam.dialog.PresetDialog;
import com.rhino.foscam.dialog.VideoSettingsDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.VideoParams;
import com.rhino.foscam.task.SaveSnapshotTask;
import com.rhino.foscam.task.sd.CameraControlTask;
import com.rhino.foscam.task.sd.IRControlTask;
import com.rhino.foscam.task.sd.LiveFeedTask;
import com.rhino.foscam.task.sd.SetPresetTask;
import com.rhino.foscam.task.sd.UpdateVideoSettingsTask;
import com.rhino.foscam.view.ZoomImageView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class LiveFeedActivity extends Activity{
	
	private final Camera camera = new Camera();
	private LiveFeedActivity context = this;
	private LiveFeedTask videoTask;
	private CameraControlTask controlTask;
	private IRControlTask irControlTask;
	private UpdateVideoSettingsTask settingsTask;
	private VideoSettingsDialog videoSettingsDialog;
	private MjpegInputStream stream = null;
	private Audio audio = new Audio();
	private Talk talk = new Talk();
	private boolean speaker = false;
	private boolean microphone = false;
	private boolean loggedIn = false;
	private boolean statusListen = true;
	private boolean openingAudio = false;
	private boolean openingTalk = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_feed);
		
		Intent intent = getIntent();
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));

		ZoomImageView video = (ZoomImageView)findViewById(R.id.video);
		videoTask = new LiveFeedTask(context, camera, video, stream);
		videoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		FSApi.Init();
		CameraStatusListener statusListenerTask = new CameraStatusListener();
		statusListenerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		FSApi.usrLogIn(0, camera.getCameraUrl(), camera.getUsername(), camera.getPassword(), 0, Integer.parseInt(camera.getPort()), 0, "", 0);
	}
	
	@Override
	public void onBackPressed() {
		cancelVideo();
		finish();
		super.onBackPressed();
	}
	
	@Override
	public void onPause() {
		cancelVideo();
		super.onPause();
		finish();
	}
	
	public void returnToCameraMenu(View v) {
		cancelVideo();
		finish();
	}
	
	public void failedClose() {
		cancelVideo();
		finish();
	}
	
	public void failedRetry() {
		cancelVideo();
		ZoomImageView video = (ZoomImageView)findViewById(R.id.video);
		videoTask = new LiveFeedTask(context, camera, video, stream);
		videoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);	
	}
	
	public void setVideoParams(VideoParams videoParams) {
		videoSettingsDialog = new VideoSettingsDialog(context, videoParams);
		
		if(!videoParams.isFlipped()) {
			setControlListener(findViewById(R.id.upButton), CameraUtilsSD.COMMAND_UP, CameraUtilsSD.COMMAND_UP_STOP);
			setControlListener(findViewById(R.id.downButton), CameraUtilsSD.COMMAND_DOWN, CameraUtilsSD.COMMAND_DOWN_STOP);
		} else {
			setControlListener(findViewById(R.id.upButton), CameraUtilsSD.COMMAND_DOWN, CameraUtilsSD.COMMAND_DOWN_STOP);
			setControlListener(findViewById(R.id.downButton), CameraUtilsSD.COMMAND_UP, CameraUtilsSD.COMMAND_UP_STOP);
		}
		if(!videoParams.isMirrored()) {
			setControlListener(findViewById(R.id.leftButton), CameraUtilsSD.COMMAND_LEFT, CameraUtilsSD.COMMAND_LEFT_STOP);
			setControlListener(findViewById(R.id.rightButton), CameraUtilsSD.COMMAND_RIGHT, CameraUtilsSD.COMMAND_RIGHT_STOP);
		} else {
			setControlListener(findViewById(R.id.leftButton), CameraUtilsSD.COMMAND_RIGHT, CameraUtilsSD.COMMAND_RIGHT_STOP);
			setControlListener(findViewById(R.id.rightButton), CameraUtilsSD.COMMAND_LEFT, CameraUtilsSD.COMMAND_LEFT_STOP);
		}
	}
	
	public void updateVideoParams(VideoParams videoParams) {
		settingsTask = new UpdateVideoSettingsTask(context, camera, videoParams);
		settingsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void restartVideo() {
		ZoomImageView video = (ZoomImageView)findViewById(R.id.video);
		videoTask = new LiveFeedTask(context, camera, video, stream);
		videoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void setControlListener(View v, final int startCommand, final int stopCommand) {
		v.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				//cancel previous camera command if still running
				if(controlTask != null) {
					controlTask.cancel(true);
				}
				
				int action = event.getAction();
				
				if(action == MotionEvent.ACTION_DOWN) {
					controlTask = new CameraControlTask(camera, startCommand);
					controlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					view.setBackgroundResource(R.drawable.button_pressed);
				} else if(action == MotionEvent.ACTION_UP) {
					controlTask = new CameraControlTask(camera, stopCommand);
					controlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					view.setBackgroundResource(R.drawable.button);
				}
				return true;
			}
		});
	}
	
	public void irOn(View v) {
		irControlTask = new IRControlTask(context, camera, CameraUtilsSD.COMMAND_IR_ON);
		irControlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void irOff(View v) {
		irControlTask = new IRControlTask(context, camera, CameraUtilsSD.COMMAND_IR_OFF);
		irControlTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}	
	
	public void IRUpdate(boolean on, boolean success) {
		if(on && success) {
			Toast toast = Toast.makeText(context, "IR Enabled", Toast.LENGTH_SHORT);
			toast.show();
		} else if(!on && success) {
			Toast toast = Toast.makeText(context, "IR Disabled", Toast.LENGTH_SHORT);
			toast.show();
		} else if(!success) {
			Toast toast = Toast.makeText(context, "IR command failed", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	public void takeSnapshot(View v) {
		ZoomImageView video = (ZoomImageView)findViewById(R.id.video);
		Bitmap frame = ((BitmapDrawable)video.getDrawable()).getBitmap();
		SaveSnapshotTask task = new SaveSnapshotTask(context, frame);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void cancelVideo() {
		audio.stop();
		talk.stop();
		FSApi.usrLogOut(0);
		
		speaker = false;
		microphone = false;
		statusListen = false;
		((ImageButton)findViewById(R.id.speakerButton)).setImageResource(R.drawable.speaker_off);
		((ImageButton)findViewById(R.id.microphoneButton)).setImageResource(R.drawable.microphone_off);
		
		if(videoTask != null) {
			videoTask.cancel(true);
		}
		if(stream != null) {
			try {
				stream.close();
			} catch (Exception e) {}
		}
	}
	
	public void showVideoSettingsDialog(View v) {
		cancelVideo();
		Dialog settingsDialog = videoSettingsDialog.createDialog();
		settingsDialog.show();
	}
	
	public void showPresetDialog(View v) {
		PresetDialog.createDialog(context).show();
	}
	
	public void goToPreset(int command) {
		CameraControlTask goToPreset = new CameraControlTask(camera, command);
		goToPreset.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void setPreset(int command) {
		SetPresetTask setPreset = new SetPresetTask(context, camera, command);
		setPreset.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void listen(View v) {
		speaker = !speaker;
		if(speaker) {
			if(loggedIn) {
				((ImageButton)v).setImageResource(R.drawable.speaker);
				audio.start();
				Toast toast = Toast.makeText(context, "Audio Stream Opened", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				openingAudio = true;
				FSApi.usrLogIn(0, camera.getCameraUrl(), camera.getUsername(), camera.getPassword(), 0, Integer.parseInt(camera.getPort()), Integer.parseInt(camera.getPort()), "", 0);
			}
		} else {
			audio.stop();
			Toast toast = Toast.makeText(context, "Audio Stream Closed", Toast.LENGTH_SHORT);
			toast.show();
			((ImageButton)v).setImageResource(R.drawable.speaker_off);
		}
	}
	
	public void talk(View v) {
		microphone = !microphone;
		if(microphone) {
			if(loggedIn) {
				((ImageButton)v).setImageResource(R.drawable.microphone);
				talk.start();
				Toast toast = Toast.makeText(context, "Talk Stream Opened", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				openingTalk = true;
				FSApi.usrLogIn(0, camera.getCameraUrl(), camera.getUsername(), camera.getPassword(), 0, Integer.parseInt(camera.getPort()), Integer.parseInt(camera.getPort()), "", 0);
			}
		} else {
			talk.stop();
			Toast toast = Toast.makeText(context, "Talk Stream Closed", Toast.LENGTH_SHORT);
			toast.show();
			((ImageButton)v).setImageResource(R.drawable.microphone_off);
		}
	}
	
	private class CameraStatusListener extends AsyncTask<Void, Integer, Integer>{
		@Override
		protected Integer doInBackground(Void... arg0) {
			while(statusListen)
			{
				for(int id=0;id<4;id++)
				{
					int statusID=FSApi.getStatusId(id);
					if(statusID<0)
					{
						try 
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}
					} else if(statusID == StatusListener.STATUS_LOGIN_SUCCESS) {
						loggedIn = true;
						if(openingAudio) {
							audio.start();
							openingAudio = false;
                            publishProgress(1);
						}
						if(openingTalk) {
							talk.start();
							openingTalk = false;
                            publishProgress(2);
						}
					} else if(statusID == StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL) {
						loggedIn = false;
						if(openingAudio) {
							openingAudio = false;
							speaker = false;
                            publishProgress(3);
						}
						if(openingTalk) {
							openingTalk = false;
							microphone = false;
                            publishProgress(4);
						}
					}
				}
			}
			return null;
		}

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(values[0] == 1) {
                Toast toast = Toast.makeText(context, "Audio Stream Opened", Toast.LENGTH_SHORT);
                toast.show();
                ((ImageButton)findViewById(R.id.speakerButton)).setImageResource(R.drawable.speaker);
            } else if(values[0] == 2) {
                Toast toast = Toast.makeText(context, "Talk Stream Opened", Toast.LENGTH_SHORT);
                toast.show();
                ((ImageButton)findViewById(R.id.microphoneButton)).setImageResource(R.drawable.microphone);
            } else if(values[0] == 3) {
                Toast toast = Toast.makeText(context, "Failed to get Audio Stream", Toast.LENGTH_SHORT);
                toast.show();
            } else if(values[0] == 4) {
                Toast toast = Toast.makeText(context, "Failed to send Talk Stream", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
	}
}

class Audio implements Runnable {
	private AVStreamData audioStreamData = new AVStreamData();
	private AudioTrack mAudioTrack;
	private boolean running = false;
	
	public void start(){
		int  minBufSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);  
		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufSize*2, AudioTrack.MODE_STREAM);
		FSApi.startAudioStream(0);
		mAudioTrack.play();
		running = true;
		new Thread(this).start();
	}
	
    public void stop() {
    	FSApi.stopAudioStream(0);
    	try {	
    		mAudioTrack.stop();
    	} catch(Exception e){}
    	mAudioTrack = null;
    	running = false;
    }
	
	public void run(){
		while (running)
		{
			try {
				FSApi.getAudioStreamData(audioStreamData ,0);
    		} catch(Exception e) {
    			continue;
    		}
			if(audioStreamData.dataLen > 0) {
				try{
					mAudioTrack.write(audioStreamData.data, 0, audioStreamData.dataLen);
				} catch  (Exception e) {}
			} else {
        		try {
        			Thread.sleep(10);
				} catch (InterruptedException e) {}
        	}
		}
	}
}

class Talk implements Runnable{
	private AudioRecord mAudioRecord;
	private boolean running = false;
	private byte []buffer = new byte[960];
	private int bytesRead = 0;
	int bufLen = 640;
	
	public void start(){
		int minBufSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, minBufSize);
		FSApi.startTalk(0);
		try {
			mAudioRecord.startRecording();
		} catch(Exception e) {}
		running = true;
		new Thread(this).start();
	}
	
    public void stop() {
    	FSApi.stopTalk(0);
    	try {
    		mAudioRecord.stop();
    		mAudioRecord.release();
    	} catch(Exception e) {}
		mAudioRecord = null;
    	running = false;
    }
	
	public void run(){
		while (running) {
			try {
				bytesRead = mAudioRecord.read(buffer, 0, bufLen);
				if( bytesRead > 0 ) {
					FSApi.sendTalkFrame(buffer, bufLen, 0);
				}
			} catch (Exception e) {}
		}
	}
}