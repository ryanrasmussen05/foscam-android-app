package com.remote.util;

import android.os.Message;
import android.util.Log;

import com.foscam.ipc.LiveView;
import com.ipc.sdk.StatusListener;

public class MyStatusListener implements StatusListener
{

	@Override
	public void OnStatusCbk(int statusID, int reserve1, int reserve2,
			int reserve3, int reserve4) 
	{
		Log.d("moon", "callback, statusID:"+statusID);
		
		if( statusID != -1 )
		{
			Message msg = LiveView.mStatusMsgHandler.obtainMessage();
			msg.arg1 = statusID;
			LiveView.mStatusMsgHandler.sendMessage(msg);
		}
		
		if( statusID == StatusListener.STATUS_LOGIN_SUCCESS )
		{
			
			Message msg = LiveView.mStatusMsgHandler.obtainMessage();
			msg.arg1 = 1000;
			LiveView.mStatusMsgHandler.sendMessage(msg);
			
			
		}
		else if( statusID==StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL )
		{
			Message msg = LiveView.mStatusMsgHandler.obtainMessage();
			msg.arg1 = 1001;
			LiveView.mStatusMsgHandler.sendMessage(msg);
			
			
		}
		
		
	}
}
