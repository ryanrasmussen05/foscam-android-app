package com.rhino.foscam.adapter;

import java.util.ArrayList;

import com.rhino.foscam.R;
import com.rhino.foscam.pojo.Camera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CameraListAdapter extends ArrayAdapter<Camera> {
	private final Context context;
	private final ArrayList<Camera> values;
	
	public CameraListAdapter(Context context, ArrayList<Camera> values) {
		super(context, R.layout.camera_list_item, values);
		this.context = context;
		this.values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewRow = inflater.inflate(R.layout.camera_list_item, parent, false);
		TextView cameraName = (TextView)viewRow.findViewById(R.id.cameraName);
		TextView cameraUrl = (TextView)viewRow.findViewById(R.id.cameraUrl);
		cameraName.setText(values.get(position).getCameraName());
		cameraUrl.setText(values.get(position).getCameraUrl() + ":" + values.get(position).getPort());
		return viewRow;
	}
}
