package com.rhino.foscam.dialog;

import com.rhino.foscam.R;
import com.rhino.foscam.activity.sd.LiveFeedActivity;
import com.rhino.foscam.pojo.sd.VideoParams;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;

public class VideoSettingsDialog{
	
	private VideoParams videoParams;
	private Dialog dialog;
	private LiveFeedActivity context;

	public VideoSettingsDialog(LiveFeedActivity context, VideoParams videoParams) {
		this.context = context;
		this.videoParams = videoParams;
	}
	
	public Dialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
		LayoutInflater inflater = context.getLayoutInflater();
		
		final View dialogContent = inflater.inflate(R.layout.video_settings, null);
		builder.setTitle("Video Settings");
		builder.setView(dialogContent);
		
		final SeekBar brightness = (SeekBar)(dialogContent.findViewById(R.id.brightness));
		brightness.setMax(15);
		brightness.setProgress(videoParams.getBrightness());
		
		final SeekBar contrast = (SeekBar)(dialogContent.findViewById(R.id.contrast));
		contrast.setMax(6);
		contrast.setProgress(videoParams.getContrast());
		
		final Spinner resolution = (Spinner)(dialogContent.findViewById(R.id.resolution));
		final Spinner mode = (Spinner)(dialogContent.findViewById(R.id.mode));
		
		resolution.setSelection(videoParams.getResolution());
		mode.setSelection(videoParams.getMode());
		
		final CheckBox flip = (CheckBox)(dialogContent.findViewById(R.id.flip));
		final CheckBox mirror = (CheckBox)(dialogContent.findViewById(R.id.mirror));
		
		flip.setChecked(videoParams.isFlipped());
		mirror.setChecked(videoParams.isMirrored());
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.restartVideo();
				dialog.cancel();
			}
		});
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				VideoParams updatedParams = new VideoParams();
				if(brightness.getProgress() != videoParams.getBrightness()) {
					updatedParams.setBrightness(brightness.getProgress());
				}
				if(contrast.getProgress() != videoParams.getContrast()) {
					updatedParams.setContrast(contrast.getProgress());
				}
				if(resolution.getSelectedItemPosition() != videoParams.getResolution()) {
					updatedParams.setResolution(resolution.getSelectedItemPosition());
				}
				if(mode.getSelectedItemPosition() != videoParams.getMode()) {
					updatedParams.setMode(mode.getSelectedItemPosition());
				}
				int updatedFlip;
				if(flip.isChecked() && mirror.isChecked()) {
					updatedFlip = 3;
				} else if(!flip.isChecked() && mirror.isChecked()) {
					updatedFlip = 2;
				} else if(flip.isChecked() && !mirror.isChecked()) {
					updatedFlip = 1;
				} else {
					updatedFlip = 0;
				}
				if(updatedFlip != videoParams.getFlip()) {
					updatedParams.setFlip(flip.isChecked(), mirror.isChecked());
				}
				
				context.updateVideoParams(updatedParams);
				dialog.cancel();
			}	
		});
		
		dialog = builder.create();
		return dialog;
	}
}
