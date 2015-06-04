package com.rhino.foscam.task.hd;

import com.rhino.foscam.accessor.CameraUtilsHD;
import com.rhino.foscam.activity.hd.FTPSettingsActivity;
import com.rhino.foscam.dialog.TestFTPDialog;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.hd.FTP;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

public class TestFTPTask extends AsyncTask<Void, Void, Integer>{

    public TestFTPDialog testDialog;
    public FTPSettingsActivity context;
    public Camera camera;
    public FTP ftp;
    public String testResult;

    public static final Integer CONNECTION_ERROR = 0;
    public static final Integer SUCCESS = 1;

    public TestFTPTask(FTPSettingsActivity context, Camera camera, FTP ftp) {
        this.context = context;
        this.camera = camera;
        this.ftp = ftp;
    }

    @Override
    protected void onPreExecute() {
        testDialog = new TestFTPDialog();
        testDialog.setCancelable(false);
        testDialog.show(context.getFragmentManager(), "test");
    }

    @Override
    protected Integer doInBackground(Void... params) {
        try {
            testResult = CameraUtilsHD.testFTP(camera, ftp);
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
            if(testResult.equals("0")) {
                Toast toast = Toast.makeText(context, "FTP Test Successful", Toast.LENGTH_LONG);
                toast.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("FTP Test Failed");
                builder.setMessage("Check the FTP server settings");
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
