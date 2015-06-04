package com.rhino.foscam.task.sd;

import com.rhino.foscam.accessor.CameraUtilsSD;
import com.rhino.foscam.activity.sd.MailSettingsActivity;
import com.rhino.foscam.dialog.TestMailDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.MailTestResult;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class TestMailTask extends AsyncTask<Void, Void, Integer>{
	
	public TestMailDialog testDialog;
	public MailSettingsActivity context;
	public Camera camera;
	public MailTestResult mailResult;
	
	public static final Integer CONNECTION_ERROR = 0;
	public static final Integer SUCCESS = 1;
	
	public TestMailTask(MailSettingsActivity context, Camera camera) {
		this.context = context;
		this.camera = camera;
	}
	
	@Override
	protected void onPreExecute() {
		testDialog = new TestMailDialog();
		testDialog.setCancelable(false);
		testDialog.show(context.getFragmentManager(), "test");
	}

	@Override
	protected Integer doInBackground(Void... params) {
		try {
			mailResult = CameraUtilsSD.testMail(camera);
		} catch (Exception e) {
			return CONNECTION_ERROR;
		}
		return SUCCESS;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		try {
			testDialog.dismiss();
		} catch(Exception e){}
		
		if(result == CONNECTION_ERROR) {
            Toast toast = Toast.makeText(context, "Error occurred, try test again", Toast.LENGTH_LONG);
            toast.show();
		} else if(result == SUCCESS) {
			if(mailResult.isSuccess()) {
	            Toast toast = Toast.makeText(context, "Mail Test Successful", Toast.LENGTH_LONG);
	            toast.show();
	            context.saveComplete();
			} else {
	            AlertDialog.Builder builder = new AlertDialog.Builder(context);
	            builder.setTitle("Mail Test Failed");
	            builder.setMessage("Cause of failure: " + mailResult.getResult());
	            builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
	            AlertDialog failDialog = builder.create();
	            failDialog.show();
			}
		}
	}
	
	@Override
	protected void onCancelled(Integer result) {
		try {
			testDialog.dismiss();
		} catch(Exception e){}
	}
}
