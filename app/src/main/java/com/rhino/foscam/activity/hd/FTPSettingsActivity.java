package com.rhino.foscam.activity.hd;

import com.rhino.foscam.R;
import com.rhino.foscam.MainMenuActivity;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.hd.FTP;
import com.rhino.foscam.task.hd.SetFTPTask;
import com.rhino.foscam.task.hd.TestFTPTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class FTPSettingsActivity extends Activity{

    private final Camera camera = new Camera();
    private final FTPSettingsActivity context = this;

    public String ftpAddr;
    public String ftpPort;
    public Integer mode;
    public String userName;
    public String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ftp_settings_hd);

        Intent intent = getIntent();

        camera.setCameraUrl(intent.getStringExtra(MainMenuActivity.CAMERA_URL_EXTRA));
        camera.setPort(intent.getStringExtra(MainMenuActivity.CAMERA_PORT_EXTRA));
        camera.setUsername(intent.getStringExtra(MainMenuActivity.CAMERA_USERNAME_EXTRA));
        camera.setPassword(intent.getStringExtra(MainMenuActivity.CAMERA_PASSWORD_EXTRA));
        camera.setType(intent.getStringExtra(MainMenuActivity.CAMERA_TYPE_EXTRA));
        ftpAddr = intent.getStringExtra(CameraMenuActivity.FTP_ADDRESS_EXTRA);
        ftpPort = intent.getStringExtra(CameraMenuActivity.FTP_PORT_EXTRA);
        mode = intent.getIntExtra(CameraMenuActivity.FTP_MODE_EXTRA, 0);
        userName = intent.getStringExtra(CameraMenuActivity.FTP_USERNAME_EXTRA);
        password = intent.getStringExtra(CameraMenuActivity.FTP_PASSWORD_EXTRA);

        ((EditText)findViewById(R.id.ftpServer)).setText(ftpAddr);
        ((EditText)findViewById(R.id.ftpPort)).setText(ftpPort);
        ((EditText)findViewById(R.id.ftpUser)).setText(userName);
        ((EditText)findViewById(R.id.ftpPassword)).setText(password);
        ((Spinner)findViewById(R.id.ftpMode)).setSelection(mode);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    public void returnToCameraMenu(View v) {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void saveComplete() {
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void test(View v) {
        FTP ftp = new FTP();
        ftp.setFtpAddr(((EditText) findViewById(R.id.ftpServer)).getText().toString());
        ftp.setFtpPort(((EditText) findViewById(R.id.ftpPort)).getText().toString());
        ftp.setUserName(((EditText) findViewById(R.id.ftpUser)).getText().toString());
        ftp.setPassword(((EditText) findViewById(R.id.ftpPassword)).getText().toString());
        ftp.setMode(String.valueOf(((Spinner) findViewById(R.id.ftpMode)).getSelectedItemPosition()));

        TestFTPTask task = new TestFTPTask(context, camera, ftp);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void save(View v) {
        FTP ftp = new FTP();
        ftp.setFtpAddr(((EditText) findViewById(R.id.ftpServer)).getText().toString());
        ftp.setFtpPort(((EditText) findViewById(R.id.ftpPort)).getText().toString());
        ftp.setUserName(((EditText) findViewById(R.id.ftpUser)).getText().toString());
        ftp.setPassword(((EditText) findViewById(R.id.ftpPassword)).getText().toString());
        ftp.setMode(String.valueOf(((Spinner) findViewById(R.id.ftpMode)).getSelectedItemPosition()));

        SetFTPTask task = new SetFTPTask(context, camera, ftp);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
