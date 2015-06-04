package com.rhino.foscam.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

public class SnapshotView extends ImageView {
	
	private String snapshotTime;
	private int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
	private int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

	public SnapshotView(Context context) {
		super(context);
		LayoutParams params = new LayoutParams(width, height);
		setLayoutParams(params);
	}
	
	public String getSnapshotTime() {
		return snapshotTime;
	}
	
	public void setSnapshotTime(String time) {
		this.snapshotTime = time;
	}
	
	public Bitmap getBitmap() {
		return ((BitmapDrawable)getDrawable()).getBitmap();
	}

}
