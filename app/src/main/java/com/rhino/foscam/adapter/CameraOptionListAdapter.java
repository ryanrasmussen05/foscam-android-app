package com.rhino.foscam.adapter;

import java.util.ArrayList;

import com.rhino.foscam.R;
import com.rhino.foscam.pojo.CameraOption;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CameraOptionListAdapter extends ArrayAdapter<CameraOption> {
	private final Context context;
	private final ArrayList<CameraOption> values;
	
	public CameraOptionListAdapter(Context context, ArrayList<CameraOption> values) {
		super(context, R.layout.camera_option_item, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewRow = inflater.inflate(R.layout.camera_option_item, parent, false);
		TextView optionName = (TextView)viewRow.findViewById(R.id.cameraOption);
		optionName.setText(values.get(position).getOptionName());
		return viewRow;
	}
}
