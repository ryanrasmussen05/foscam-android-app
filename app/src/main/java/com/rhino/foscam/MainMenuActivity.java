package com.rhino.foscam;

import java.util.ArrayList;

import com.foscam.ipc.LiveView;
import com.rhino.foscam.R;
import com.rhino.foscam.adapter.CameraListAdapter;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.task.AddCameraTask;
import com.rhino.foscam.task.EditCameraTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainMenuActivity extends FragmentActivity {
	
	private final ArrayList<Camera> cameraList = new ArrayList<Camera>();
	private CameraListAdapter adapter;
	private ListView listView;
	private final MainMenuActivity context = this;
	private AlertDialog cameraOptionsDialog;
	
	public static final String CAMERAS = "cameraList";
	public static final String FIELD_SEPARATOR = "<<field>>";
	public static final String CAMERA_SEPARATOR = "<<<cam>>>";
	
	public static final String CAMERA_NAME_EXTRA = "cameraNameExtra";
	public static final String CAMERA_URL_EXTRA = "cameraUrlExtra";
	public static final String CAMERA_PORT_EXTRA = "cameraPortExtra";
	public static final String CAMERA_USERNAME_EXTRA = "cameraUsernameExtra";
	public static final String CAMERA_PASSWORD_EXTRA = "cameraPasswordExtra";
	public static final String CAMERA_TYPE_EXTRA = "cameraTypeExtra";
	
	SharedPreferences data; 
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		data = getPreferences(Context.MODE_PRIVATE);
		editor = data.edit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Rate");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=com.rhino.foscam"));
		startActivity(intent);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		createCameraList();
	}
	
	public void showAddCameraDialog(View v) {
		showCameraOptionsDialog(false, 0);
	}
	
	public void launchIPC(View v) {
		Intent intent = new Intent(context, LiveView.class);
		startActivity(intent);
	}
	
	public boolean isDuplicate(String name) {
		String cameras = data.getString(CAMERAS, "");
		ArrayList<String> cameraNames = new ArrayList<String>();
		
		String[] seperated = cameras.split(CAMERA_SEPARATOR);
		
		for(String cameraData : seperated) {
			String[] fields = cameraData.split(FIELD_SEPARATOR);
			cameraNames.add(fields[0]);
		}
		
		return cameraNames.contains(name);
	}
	
	public void addCamera(Camera camera, String message) {
		String cameraString = camera.getCameraName() + FIELD_SEPARATOR + camera.getCameraUrl() + FIELD_SEPARATOR + camera.getPort() + FIELD_SEPARATOR + camera.getUsername() + FIELD_SEPARATOR + camera.getPassword() + FIELD_SEPARATOR + camera.getType() + CAMERA_SEPARATOR;
		String savedCameras = data.getString(CAMERAS, "");
		savedCameras = savedCameras + cameraString;
		editor.putString(CAMERAS, savedCameras);
		editor.commit();
		cameraOptionsDialog.dismiss();
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		context.createCameraList();
	}
	
	public void editCamera(String oldName, String newName, String url, String port, String user, String password, String type) {
		String oldCameraString = data.getString(CAMERAS, "");
		String newCameraString = "";
		
		String[] cameraStrings = oldCameraString.split(CAMERA_SEPARATOR);
		
		for(String camera : cameraStrings) {
			String[] cameraFields = camera.split(FIELD_SEPARATOR);
			
			if(oldName.equals(cameraFields[0])) {
				String newCamera = newName + FIELD_SEPARATOR + url + FIELD_SEPARATOR + port + FIELD_SEPARATOR + user + FIELD_SEPARATOR + password + FIELD_SEPARATOR + type + CAMERA_SEPARATOR;
				newCameraString = newCameraString + newCamera;
			} else {
				newCameraString = newCameraString + camera + CAMERA_SEPARATOR;
			}
		}
		
		editor.putString(CAMERAS, newCameraString);
		editor.commit();
		cameraOptionsDialog.dismiss();
		Toast.makeText(context, "Camera Updated", Toast.LENGTH_SHORT).show();
		context.createCameraList();
	}
	
	public void deleteCamera(String name) {
		String oldCameraString = data.getString(CAMERAS, "");
		String newCameraString = "";
		
		String[] cameraStrings = oldCameraString.split(CAMERA_SEPARATOR);
		
		for(String camera : cameraStrings) {
			String[] cameraFields = camera.split(FIELD_SEPARATOR);
			
			if(!name.equals(cameraFields[0])) {
				newCameraString = newCameraString + camera + CAMERA_SEPARATOR;
			}
		}
		
		editor.putString(CAMERAS, newCameraString);
		editor.commit();
	}
	
	public String getCameraUrl(String name) {
		return getCameraFieldByIndex(name, 1);
	}
	
	public String getCameraPort(String name) {
		return getCameraFieldByIndex(name, 2);
	}
	
	public String getCameraUserName(String name) {
		return getCameraFieldByIndex(name, 3);
	}
	
	public String getCameraPassword(String name) {
		return getCameraFieldByIndex(name, 4);
	}
	
	public String getCameraType(String name) {
		return getCameraFieldByIndex(name, 5);
	}
	
	public String getCameraFieldByIndex(String name, int index) {
		String cameraString = data.getString(CAMERAS, "");
		
		String[] cameras = cameraString.split(CAMERA_SEPARATOR);
		
		for(String camera : cameras) {
			String[] cameraFields = camera.split(FIELD_SEPARATOR);
			if(name.equals(cameraFields[0])) {
				if(index == 4 && cameraFields.length < 5) {
					return "";
				} else if(index ==5 && cameraFields.length < 6) {
					return "0";
				}
				return cameraFields[index];
			}
		}
		
		return "";
	}
	
	public void createCameraList() {
		cameraList.clear();
		adapter = new CameraListAdapter(this, cameraList);
		listView = (ListView)findViewById(R.id.cameraList);
		listView.setAdapter(adapter);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view, int cameraListIndex, long arg3) {
				view.performHapticFeedback(0);
				showEditCameraDialog(cameraListIndex);
				return true;
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int cameraListIndex, long arg3) {
				startCameraActivity(cameraList.get(cameraListIndex));
			}
		});
		
		String cameraTempList = data.getString(CAMERAS, "");
		String[] cameras = cameraTempList.split(CAMERA_SEPARATOR);
		
		if(cameras[0].length() > 0) {
			for(String camera : cameras) {
				String[] cameraFields = camera.split(FIELD_SEPARATOR);
				
				Camera c = new Camera();
				c.setCameraName(cameraFields[0]);
				c.setCameraUrl(cameraFields[1]);
				c.setPort(cameraFields[2]);
				c.setUsername(cameraFields[3]);
				
				if(cameraFields.length > 4) {
					c.setPassword(cameraFields[4]);
				} else {
					c.setPassword("");
				}
				
				if(cameraFields.length > 5) {
					c.setType(cameraFields[5]);
				} else {
					c.setType("0");
				}
				
				cameraList.add(c);
			}
		}
		
		adapter.notifyDataSetChanged();
	}
	
	public void showCameraOptionsDialog(boolean edit, int index) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
		LayoutInflater inflater = this.getLayoutInflater();
		
		final View view = inflater.inflate(R.layout.add_camera, null);
		final boolean fromEdit = edit;
		String old_name = "";
		
		if(fromEdit) {
			builder.setTitle("Edit Camera");
			old_name = cameraList.get(index).getCameraName();
		} else {
			builder.setTitle("Add Camera");
		}
		
		final String oldName = old_name;
		
		builder.setView(view);
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {}	
		});
				
		cameraOptionsDialog = builder.create();
		
		if(fromEdit) {
			EditText cameraName = (EditText)view.findViewById(R.id.cameraName);
			EditText cameraUrl = (EditText)view.findViewById(R.id.cameraIp);
			EditText cameraPort = (EditText)view.findViewById(R.id.port);
			EditText cameraUser = (EditText)view.findViewById(R.id.username);
			EditText cameraPassword = (EditText)view.findViewById(R.id.password);
			Spinner cameraType = (Spinner)view.findViewById(R.id.cameraType);
			cameraName.setText(oldName);
			cameraUrl.setText(getCameraUrl(oldName));
			cameraPort.setText(getCameraPort(oldName));
			cameraUser.setText(getCameraUserName(oldName));
			cameraPassword.setText(getCameraPassword(oldName));
			cameraType.setSelection(Integer.parseInt(getCameraType(oldName)));
		}
		
		cameraOptionsDialog.show();
		
		cameraOptionsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText cameraName = (EditText)view.findViewById(R.id.cameraName);
				EditText cameraUrl = (EditText)view.findViewById(R.id.cameraIp);
				EditText cameraPort = (EditText)view.findViewById(R.id.port);
				EditText cameraUser = (EditText)view.findViewById(R.id.username);
				EditText cameraPassword = (EditText)view.findViewById(R.id.password);
				Spinner cameraType = (Spinner)view.findViewById(R.id.cameraType);
				
				String name = cameraName.getText().toString();
				String url = cameraUrl.getText().toString();
				String port = cameraPort.getText().toString();
				String user = cameraUser.getText().toString();
				String password = cameraPassword.getText().toString();
				String type = String.valueOf(cameraType.getSelectedItemPosition());
				
				boolean duplicate = false;
				if(fromEdit) {
					if(!oldName.equals(name)){
						duplicate = isDuplicate(name);
					}
				} else {
					duplicate = isDuplicate(name);
				}
				
				if(name.equals("")) {
					Toast toast = Toast.makeText(context, "Camera Name is required", Toast.LENGTH_LONG);
					toast.show();
				} else if (url.equals("")) {
					Toast toast = Toast.makeText(context, "Camera IP/Url is required", Toast.LENGTH_LONG);
					toast.show();
				} else if (port.equals("")) {
					Toast toast = Toast.makeText(context, "Port is required", Toast.LENGTH_LONG);
					toast.show();
				} else if (user.equals("")) {
					Toast toast = Toast.makeText(context, "Username is required", Toast.LENGTH_LONG);
					toast.show();
				} else if (duplicate) {
					Toast toast = Toast.makeText(context, "Camera Name '" + name + "' already exists", Toast.LENGTH_LONG);
					toast.show();
				} else if (!fromEdit) {
					AddCameraTask task = new AddCameraTask(context, name, url, port, user, password, type);
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else if (fromEdit) {
					EditCameraTask task = new EditCameraTask(context, oldName, name, url, port, user, password, type);
					task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
		});
	}
	
	public void showEditCameraDialog(final int index) {
		final String cameraToEdit = cameraList.get(index).getCameraName();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Options (" + cameraToEdit + ")");
		String[] options = new String[2];
		options[0] = "Edit";
		options[1] = "Delete";
		builder.setItems(options, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0) {
					showCameraOptionsDialog(true, index);
				} else {
					deleteCamera(cameraToEdit);
					createCameraList();
				}
			}
		});
		Dialog dialog = builder.create();
		dialog.setCancelable(true);
		dialog.show();
	}
	
	public void startCameraActivity(Camera camera) {
		Intent intent;
		if(camera.isHD()) {
			intent = new Intent(context, com.rhino.foscam.activity.hd.CameraMenuActivity.class);
		} else {
			intent = new Intent(context, com.rhino.foscam.activity.sd.CameraMenuActivity.class);
		}
		intent.putExtra(CAMERA_NAME_EXTRA, camera.getCameraName());
		intent.putExtra(CAMERA_URL_EXTRA, camera.getCameraUrl());
		intent.putExtra(CAMERA_PORT_EXTRA, camera.getPort());
		intent.putExtra(CAMERA_USERNAME_EXTRA, camera.getUsername());
		intent.putExtra(CAMERA_PASSWORD_EXTRA, camera.getPassword());
		intent.putExtra(CAMERA_TYPE_EXTRA, camera.getType());
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
	}
	
}
