package com.rhino.foscam;

import com.rhino.foscam.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeScreenActivity extends Activity{
	
	private static final int WELCOME_TIMEOUT = 1000;
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_screen);
		
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	Intent intent = new Intent(context, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        }, WELCOME_TIMEOUT);
	}

}
