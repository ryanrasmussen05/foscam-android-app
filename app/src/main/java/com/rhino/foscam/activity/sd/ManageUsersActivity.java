package com.rhino.foscam.activity.sd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.task.sd.RebootCameraTask;
import com.rhino.foscam.task.sd.SetUsersTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class ManageUsersActivity extends Activity{
	
	private final Camera camera = new Camera();
	private final ManageUsersActivity context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_users);
		
		Intent intent = getIntent();
		
		camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
		camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
		camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
		camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
		camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
		
		((EditText)findViewById(R.id.username1)).setText(intent.getStringExtra(CameraMenuActivity.USER_1_NAME_EXTRA));
		((EditText)findViewById(R.id.password1)).setText(intent.getStringExtra(CameraMenuActivity.USER_1_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv1)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_1_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username2)).setText(intent.getStringExtra(CameraMenuActivity.USER_2_NAME_EXTRA));
		((EditText)findViewById(R.id.password2)).setText(intent.getStringExtra(CameraMenuActivity.USER_2_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv2)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_2_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username3)).setText(intent.getStringExtra(CameraMenuActivity.USER_3_NAME_EXTRA));
		((EditText)findViewById(R.id.password3)).setText(intent.getStringExtra(CameraMenuActivity.USER_3_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv3)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_3_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username4)).setText(intent.getStringExtra(CameraMenuActivity.USER_4_NAME_EXTRA));
		((EditText)findViewById(R.id.password4)).setText(intent.getStringExtra(CameraMenuActivity.USER_4_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv4)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_4_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username5)).setText(intent.getStringExtra(CameraMenuActivity.USER_5_NAME_EXTRA));
		((EditText)findViewById(R.id.password5)).setText(intent.getStringExtra(CameraMenuActivity.USER_5_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv5)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_5_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username6)).setText(intent.getStringExtra(CameraMenuActivity.USER_6_NAME_EXTRA));
		((EditText)findViewById(R.id.password6)).setText(intent.getStringExtra(CameraMenuActivity.USER_6_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv6)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_6_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username7)).setText(intent.getStringExtra(CameraMenuActivity.USER_7_NAME_EXTRA));
		((EditText)findViewById(R.id.password7)).setText(intent.getStringExtra(CameraMenuActivity.USER_7_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv7)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_7_PRIV_EXTRA, 0));
		((EditText)findViewById(R.id.username8)).setText(intent.getStringExtra(CameraMenuActivity.USER_8_NAME_EXTRA));
		((EditText)findViewById(R.id.password8)).setText(intent.getStringExtra(CameraMenuActivity.USER_8_PASSWORD_EXTRA));
		((Spinner)findViewById(R.id.priv8)).setSelection(intent.getIntExtra(CameraMenuActivity.USER_8_PRIV_EXTRA, 0));
	}
	
	@Override
	public void onBackPressed() {
		setResult(1);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
		super.onBackPressed();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		setResult(1);
		finish();
	}
	
	public void returnToCameraMenu(View v) {
		setResult(1);
		finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
	}
	
	public void saveChanges(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Users");
        builder.setMessage("Caution.  Editing users will cause the camera to reboot.  You will be logged out and may have to edit the camera to match the new settings.");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				continueSave();
				dialog.dismiss();
			}
		});
        AlertDialog dialog = builder.create();
        dialog.show();
	}
	
	public void continueSave() {
		CameraParams params = new CameraParams();
		params.setUser1Name(((EditText)findViewById(R.id.username1)).getText().toString());
		params.setUser1Password(((EditText)findViewById(R.id.password1)).getText().toString());
		params.setUser1Priv(((Spinner)findViewById(R.id.priv1)).getSelectedItemPosition());
		params.setUser2Name(((EditText)findViewById(R.id.username2)).getText().toString());
		params.setUser2Password(((EditText)findViewById(R.id.password2)).getText().toString());
		params.setUser2Priv(((Spinner)findViewById(R.id.priv2)).getSelectedItemPosition());
		params.setUser3Name(((EditText)findViewById(R.id.username3)).getText().toString());
		params.setUser3Password(((EditText)findViewById(R.id.password3)).getText().toString());
		params.setUser3Priv(((Spinner)findViewById(R.id.priv3)).getSelectedItemPosition());
		params.setUser4Name(((EditText)findViewById(R.id.username4)).getText().toString());
		params.setUser4Password(((EditText)findViewById(R.id.password4)).getText().toString());
		params.setUser4Priv(((Spinner)findViewById(R.id.priv4)).getSelectedItemPosition());
		params.setUser5Name(((EditText)findViewById(R.id.username5)).getText().toString());
		params.setUser5Password(((EditText)findViewById(R.id.password5)).getText().toString());
		params.setUser5Priv(((Spinner)findViewById(R.id.priv5)).getSelectedItemPosition());
		params.setUser6Name(((EditText)findViewById(R.id.username6)).getText().toString());
		params.setUser6Password(((EditText)findViewById(R.id.password6)).getText().toString());
		params.setUser6Priv(((Spinner)findViewById(R.id.priv6)).getSelectedItemPosition());
		params.setUser7Name(((EditText)findViewById(R.id.username7)).getText().toString());
		params.setUser7Password(((EditText)findViewById(R.id.password7)).getText().toString());
		params.setUser7Priv(((Spinner)findViewById(R.id.priv7)).getSelectedItemPosition());
		params.setUser8Name(((EditText)findViewById(R.id.username8)).getText().toString());
		params.setUser8Password(((EditText)findViewById(R.id.password8)).getText().toString());
		params.setUser8Priv(((Spinner)findViewById(R.id.priv8)).getSelectedItemPosition());
		SetUsersTask task = new SetUsersTask(context, camera, params);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void saveComplete() {
		RebootCameraTask task = new RebootCameraTask(context, camera);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void rebootCamera() {
		setResult(0);
		finish();
	}

}
