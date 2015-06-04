package com.rhino.foscam.dialog;

import java.util.ArrayList;

import com.rhino.foscam.R;
import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.LiveFeedActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;

public class PresetDialog{
	
	public static Dialog createDialog(final LiveFeedActivity context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
		LayoutInflater inflater = context.getLayoutInflater();
		
		final View dialogContent = inflater.inflate(R.layout.preset_dialog, null);
		builder.setTitle("Presets (Hold to Set)");
		builder.setView(dialogContent);
		
		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.restartVideo();
				dialog.cancel();
			}
		});
		
		final Dialog presetDialog = builder.create();
		
		ArrayList<View> buttons = new ArrayList<View>();
		buttons.add(dialogContent.findViewById(R.id.preset1));
		buttons.add(dialogContent.findViewById(R.id.preset2));
		buttons.add(dialogContent.findViewById(R.id.preset3));
		buttons.add(dialogContent.findViewById(R.id.preset4));
		buttons.add(dialogContent.findViewById(R.id.preset5));
		buttons.add(dialogContent.findViewById(R.id.preset6));
		buttons.add(dialogContent.findViewById(R.id.preset7));
		buttons.add(dialogContent.findViewById(R.id.preset8));
		
		for(View button : buttons) {
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					int presetIndex = Integer.parseInt((String) view.getTag());
					int command = getCommand(false, presetIndex);
					context.goToPreset(command);
					presetDialog.cancel();
				}
			});
			
			button.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					view.performHapticFeedback(0);
					int presetIndex = Integer.parseInt((String) view.getTag());
					int command = getCommand(true, presetIndex);
					context.setPreset(command);
					presetDialog.cancel();
					return true;
				}
			});
		}
		
		return presetDialog;
	}
	
	private static int getCommand(boolean set, int index) {
		if(set) {
			switch(index) {
			case 1: 
				return CameraUtilsSD.COMMAND_PRESET_SET_1;
			case 2:
				return CameraUtilsSD.COMMAND_PRESET_SET_2;
			case 3:
				return CameraUtilsSD.COMMAND_PRESET_SET_3;
			case 4:
				return CameraUtilsSD.COMMAND_PRESET_SET_4;
			case 5:
				return CameraUtilsSD.COMMAND_PRESET_SET_5;
			case 6:
				return CameraUtilsSD.COMMAND_PRESET_SET_6;
			case 7:
				return CameraUtilsSD.COMMAND_PRESET_SET_7;
			case 8:
				return CameraUtilsSD.COMMAND_PRESET_SET_8;
			}
		} else {
			switch(index) {
			case 1: 
				return CameraUtilsSD.COMMAND_PRESET_GOTO_1;
			case 2:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_2;
			case 3:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_3;
			case 4:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_4;
			case 5:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_5;
			case 6:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_6;
			case 7:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_7;
			case 8:
				return CameraUtilsSD.COMMAND_PRESET_GOTO_8;
			}
		}
		
		return CameraUtilsSD.COMMAND_PRESET_GOTO_1;
	}
}