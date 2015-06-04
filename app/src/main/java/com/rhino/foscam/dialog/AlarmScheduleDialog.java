package com.rhino.foscam.dialog;

import java.util.ArrayList;

import com.rhino.foscam.R;
import com.rhino.foscam.activity.sd.AlarmSettingsActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.AlarmSchedule;
import com.rhino.foscam.task.sd.SetScheduleTask;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class AlarmScheduleDialog extends DialogFragment {
	
	private AlarmSchedule schedule;
	private View dialog;
	
	public AlarmScheduleDialog() {
		this.schedule = null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Spinner daySpinner = (Spinner)dialog.findViewById(R.id.day);
		daySpinner.setSelection(0);
		updateChart();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		dialog = inflater.inflate(R.layout.alarm_schedule, container, false);
		
		Button selectAllButton = (Button)dialog.findViewById(R.id.selectAll);
		selectAllButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectAll();
			}
		});
		
		Button clearAllButton = (Button)dialog.findViewById(R.id.clearAll);
		clearAllButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearAll();
			}
		});
		
		Button saveButton = (Button)dialog.findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
		
		Button cancelButton = (Button)dialog.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		
		ImageView backImage = (ImageView)dialog.findViewById(R.id.backButtonSchedule);
		backImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cancel();
			}
		});
		
		TextView backText = (TextView)dialog.findViewById(R.id.headerSchedule);
		backText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				cancel();
			}
		});
		
		final Spinner daySpinner = (Spinner)dialog.findViewById(R.id.day);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.days, R.layout.large_spinner);
		adapter.setDropDownViewResource(R.layout.large_spinner);
		daySpinner.setAdapter(adapter);
		updateChart();
		
		daySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				updateChart();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				updateChart();
			}
		});
	
		ArrayList<View> boxes = new ArrayList<View>();
		dialog.findViewsWithText(boxes, getResources().getString(R.string.alarm_active), View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		
		for(View view : boxes) {
			view.setTag("black");
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String viewId = getResources().getResourceEntryName(v.getId());
					viewId = viewId.replace("time", "");
					int timeIndex = Integer.parseInt(viewId);
					
					if(v.getTag().equals("black")) {
						v.setTag("blue");
						((ImageView)v).setBackgroundResource(R.drawable.blue_box_border);
						setTimeSlot(timeIndex, true);
					} else {
						v.setTag("black");
						((ImageView)v).setBackgroundResource(R.drawable.black_box_border);
						setTimeSlot(timeIndex, false);
					}
				}
			});
		}
		
		ArrayList<View> rows = new ArrayList<View>();
		dialog.findViewsWithText(rows, getResources().getString(R.string.time_row), View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		
		for(View row : rows) {
			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View rowView) {
					int rowNumber = Integer.parseInt((String)rowView.getTag());
					
					int resourceId1 = getResources().getIdentifier("time" + (rowNumber * 4), "id", getActivity().getPackageName());
					int resourceId2 = getResources().getIdentifier("time" + ((rowNumber * 4) + 1), "id", getActivity().getPackageName());
					int resourceId3 = getResources().getIdentifier("time" + ((rowNumber * 4) + 2), "id", getActivity().getPackageName());
					int resourceId4 = getResources().getIdentifier("time" + ((rowNumber * 4) + 3), "id", getActivity().getPackageName());
					ImageView box1 = (ImageView)dialog.findViewById(resourceId1);
					ImageView box2 = (ImageView)dialog.findViewById(resourceId2);
					ImageView box3 = (ImageView)dialog.findViewById(resourceId3);
					ImageView box4 = (ImageView)dialog.findViewById(resourceId4);
					
					boolean activate = false;
					
					if(box1.getTag().equals("black") || box2.getTag().equals("black") || box3.getTag().equals("black") || box4.getTag().equals("black")){
						activate = true;
					}
					
					if(activate) {
						box1.setTag("blue");
						box2.setTag("blue");
						box3.setTag("blue");
						box4.setTag("blue");
						((ImageView)box1).setBackgroundResource(R.drawable.blue_box_border);
						((ImageView)box2).setBackgroundResource(R.drawable.blue_box_border);
						((ImageView)box3).setBackgroundResource(R.drawable.blue_box_border);
						((ImageView)box4).setBackgroundResource(R.drawable.blue_box_border);
					} else {
						box1.setTag("black");
						box2.setTag("black");
						box3.setTag("black");
						box4.setTag("black");
						((ImageView)box1).setBackgroundResource(R.drawable.black_box_border);
						((ImageView)box2).setBackgroundResource(R.drawable.black_box_border);
						((ImageView)box3).setBackgroundResource(R.drawable.black_box_border);
						((ImageView)box4).setBackgroundResource(R.drawable.black_box_border);
					}
					
					setTimeSlot((rowNumber * 4), activate);
					setTimeSlot(((rowNumber * 4) + 1), activate);
					setTimeSlot(((rowNumber * 4) + 2), activate);
					setTimeSlot(((rowNumber * 4) + 3), activate);
				}
			});
		}
		
		return dialog;
	}
	
	public void setSchedule(AlarmSchedule schedule) {
		this.schedule = schedule;
	}
	
	public void selectAll() {
		ArrayList<View> boxes = new ArrayList<View>();
		dialog.findViewsWithText(boxes, getResources().getString(R.string.alarm_active), View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		for(View view : boxes) {
			view.setTag("blue");
			((ImageView)view).setBackgroundResource(R.drawable.blue_box_border);
		}
		setSelectedDay(true);
	}
	
	public void clearAll() {
		ArrayList<View> boxes = new ArrayList<View>();
		dialog.findViewsWithText(boxes, getResources().getString(R.string.alarm_active), View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
		for(View view : boxes) {
			view.setTag("black");
			((ImageView)view).setBackgroundResource(R.drawable.black_box_border);
		}
		setSelectedDay(false);
	}
	
	public void save() {
		AlarmSettingsActivity context= (AlarmSettingsActivity)getActivity();
		Camera camera = context.getCamera();
		SetScheduleTask task = new SetScheduleTask(context, camera, schedule);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void cancel() {
		AlarmSettingsActivity context = (AlarmSettingsActivity)getActivity();
		context.closeScheduleDialog();
	}
	
	public boolean[] getSelectedDaySlots() {
		Spinner daySpinner = (Spinner)dialog.findViewById(R.id.day);
		int selectedDay = daySpinner.getSelectedItemPosition();
		
		boolean[] armed = null;
		
		switch(selectedDay) {
		case 0: 
			armed = schedule.getSunday();
			break;
		case 1:
			armed = schedule.getMonday();
			break;
		case 2:
			armed = schedule.getTuesday();
			break;
		case 3:
			armed = schedule.getWednesday();
			break;
		case 4:
			armed = schedule.getThursday();
			break;
		case 5:
			armed = schedule.getFriday();
			break;
		case 6:
			armed = schedule.getSaturday();
			break;
		}
		return armed;
	}
	
	public void setSelectedDay(boolean set) {
		boolean[] armed = getSelectedDaySlots();
		for(int index = 0; index < 96; index++) {
			armed[index] = set;
		}
	}
	
	public void updateChart() {
		
		boolean[] armed = getSelectedDaySlots();
		
		for(int index = 0; index <= 95; index++) {
			int resourceId = getResources().getIdentifier("time" + index, "id", getActivity().getPackageName());
			ImageView box = (ImageView)dialog.findViewById(resourceId);
			
			if(armed[index]) {
				box.setTag("blue");
				box.setBackgroundResource(R.drawable.blue_box_border);
			} else {
				box.setTag("black");
				box.setBackgroundResource(R.drawable.black_box_border);
			}
		}
	}
	
	public void setTimeSlot(int index, boolean set) {
		boolean[] slots = getSelectedDaySlots();
		slots[index] = set;
	}

}
