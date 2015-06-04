package com.rhino.foscam.adapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.rhino.foscam.AlarmSnapshotsActivity;
import com.rhino.foscam.accessor.StorageUtils;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.view.SnapshotView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GalleryAdapter extends BaseAdapter {
	
	private AlarmSnapshotsActivity context;
	private Camera camera;
	public ArrayList<SnapshotView> snapshots = new ArrayList<SnapshotView>();
	
	public GalleryAdapter(AlarmSnapshotsActivity context, Camera camera) {
		this.context = context;
		this.camera = camera;
		populateSnapshots();
	}
	
	private void populateSnapshots() {
		String dir = StorageUtils.PICTURE_DIR + File.separator + camera.getCameraName();
		File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
		String[] files = folder.list();
		
		for(String file : files) {
			try {
				File snapshotFile = new File(folder, file);
				Bitmap snapshotBitmap;
				snapshotBitmap = BitmapFactory.decodeFile(snapshotFile.getCanonicalPath());
				SnapshotView view = new SnapshotView(context);
				view.setImageBitmap(snapshotBitmap);
				Date lastMod = new Date(snapshotFile.lastModified());
				SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy   h:mm a");
				String formattedDate = sdf.format(lastMod);
				view.setSnapshotTime(formattedDate);
				snapshots.add(view);
			} catch(IOException e){}
		}
	}
	
	public int getCount() {
		return snapshots.size();
	}

	public Object getItem(int index) {
		return null;
	}
	
	public long getItemId(int index) {
		return 0;
	}
	
	public View getView(int position, View view, ViewGroup viewGroup) {
		return snapshots.get(snapshots.size() - position - 1);
	}

}
