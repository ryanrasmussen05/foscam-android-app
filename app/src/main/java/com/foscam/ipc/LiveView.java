package com.foscam.ipc;

import java.io.File;
import java.lang.reflect.Field;

import java.nio.ByteBuffer;

import com.decoder.util.DecH264;
import com.rhino.foscam.R;
import com.ipc.sdk.AVStreamData;
import com.ipc.sdk.FSApi;
import com.ipc.sdk.StatusListener;
import com.remote.util.ActivtyUtil;
import com.remote.util.IPCameraInfo;
import com.remote.util.MyStatusListener;

import android.graphics.Matrix;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LiveView extends Activity {

	/*
	 * sdk support max 4 channel ,this demo use only one ,so ID set 0.
	 * 
	 * Record only support H264 model.
	 * 
	 */
	Context mContext;
	boolean IsRun=false;
	boolean isInLiveViewPage = true;
	VideoView vv;
	Audio mAudio = new Audio();
	Talk mTalk = new Talk();
	View view_top_btn_array;
	View view_bottom_btn_array;
	
	boolean hasTouchMoved;
	float startX = 0;
	float startY = 0;
	float endX = 0;
	float endY = 0;
	float moveDistanceX = 0;
	float moveDistanceY = 0;
	double angel = 0.0;
	Handler ptzHandler = new Handler();
	Runnable ptzStopRunnable = new Runnable(){
		public void run()
		{
			FSApi.ptzStopRun(0);
		}
	};
	private GestureDetector mGestureDetector;
	
	
	private SoundPool sp;
	private int music;
	
	private int dispMode = 0; 
	
	LinearLayout layout;
	Button btn_snap;
	Button btn_audio; 
	private int audioState = 0;
	Button btn_talk; 
	private int talkState = 0;
	Button btn_device; 
	Button btn_exit;
	
	Button btn_Record;
	TextView tv_devName;
	
	public  static Handler mStatusMsgHandler;
	
	private IPCameraInfo lastConnectIpcInfo = new IPCameraInfo();
	private IPCameraInfo lastConnectIpcInfoTemp = new IPCameraInfo();
	private boolean hasConnected = false;
	private boolean IsRecord=false;
	
	private boolean isVVStarted = false;
	private boolean isVideoStreamStarted = false;
	
	
	
	int cursor_id = -1;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        
        mContext=this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);     
        
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );

        FSApi.Init();
        IsRun=true;
        
    	isInLiveViewPage = true;
        
    	lastConnectIpcInfo.devType = 0;
        lastConnectIpcInfo.devName = "";
        lastConnectIpcInfo.ip = "";
        lastConnectIpcInfo.streamType = 0;
        lastConnectIpcInfo.webport = 0;
        lastConnectIpcInfo.mediaport = 0;
        lastConnectIpcInfo.uid = "";
        lastConnectIpcInfo.userName = "";
        lastConnectIpcInfo.password = "";
        
        lastConnectIpcInfoTemp.devType = 0;
        lastConnectIpcInfoTemp.devName = "";
        lastConnectIpcInfoTemp.ip = "";
        lastConnectIpcInfoTemp.streamType = 0;
        lastConnectIpcInfoTemp.webport = 0;
        lastConnectIpcInfoTemp.mediaport = 0;
        lastConnectIpcInfoTemp.uid = "";
        lastConnectIpcInfoTemp.userName = "";
        lastConnectIpcInfoTemp.password = "";
    	
        
        
        mStatusMsgHandler = new Handler(){
    		public void handleMessage(Message msg) {
    			String promoteString = "";
    			switch (msg.arg1) {
                case StatusListener.STATUS_LOGIN_SUCCESS:
                	promoteString = mContext.getResources().getString(R.string.login_promote_success);
                    break;
                case StatusListener.STATUS_LOGIN_FAIL_USR_PWD_ERROR:
                	promoteString = mContext.getResources().getString(R.string.login_promote_fail_usr_pwd_error);
                	break;
                case StatusListener.STATUS_LOGIN_FAIL_ACCESS_DENY:
                	promoteString = mContext.getResources().getString(R.string.login_promote_fail_access_deny);
                	break;
                case StatusListener.STATUS_LOGIN_FAIL_EXCEED_MAX_USER:
                	promoteString = mContext.getResources().getString(R.string.login_promote_fail_exceed_max_user);
                	break;
                case StatusListener.STATUS_LOGIN_FAIL_CONNECT_FAIL:
                	promoteString = mContext.getResources().getString(R.string.login_promote_fail_connect_fail);
                	break;
                case StatusListener.FS_API_STATUS_OPEN_TALK_SUCCESS:
                	promoteString = mContext.getResources().getString(R.string.open_talk_promote_success);
                	break;
                case StatusListener.FS_API_STATUS_OPEN_TALK_FAIL_USED_BY_ANOTHER_USER:
                	promoteString = mContext.getResources().getString(R.string.open_talk_promote_fail);
                	break;
                case StatusListener.FS_API_STATUS_CLOSE_TALK_SUCCESS:
                	if( hasConnected )
                	{
                		promoteString = mContext.getResources().getString(R.string.close_talk_promote_success);
                	}
                	break;
                case StatusListener.FS_API_STATUS_CLOSE_TALK_FAIL:
                	promoteString = mContext.getResources().getString(R.string.close_talk_promote_fail);
                	break;
                case 1000:
                	hasConnected = true;
        			vv.startVideoStream();
        			
        			//save device info
        			lastConnectIpcInfo.devType = lastConnectIpcInfoTemp.devType;
        			lastConnectIpcInfo.devName = lastConnectIpcInfoTemp.devName;
        			lastConnectIpcInfo.streamType = lastConnectIpcInfoTemp.streamType;
        			lastConnectIpcInfo.ip = lastConnectIpcInfoTemp.ip;
        			lastConnectIpcInfo.webport = lastConnectIpcInfoTemp.webport;
        			lastConnectIpcInfo.mediaport = lastConnectIpcInfoTemp.mediaport;
        			lastConnectIpcInfo.uid = lastConnectIpcInfoTemp.uid;
        			lastConnectIpcInfo.userName = lastConnectIpcInfoTemp.userName;
        			lastConnectIpcInfo.password = lastConnectIpcInfoTemp.password;
                	
                	break;
                case 1001:
                	
                	hasConnected = false;
        			vv.clearScreen();
                	
                	break;
                
                	
                	
                default:
                	promoteString = "";
                    break;
    			}
    			
    			if( isInLiveViewPage )
    			{
    				if( !"".equals(promoteString) )
    				{
    					ActivtyUtil.openToast(mContext, promoteString );
    				}
    			}
    		}
    	};
        
        
        
        
        
        final MyStatusListener statusListener = new MyStatusListener();
        
        new Thread(new Runnable()
        {
			@Override
			public void run() 
			{
				int id;
				int StatusID;
				while(IsRun)
				{
					for(id=0;id<4;id++)
					{
						StatusID=FSApi.getStatusId(id);
						
						if(StatusID<0)
						{
							try 
							{
								Thread.sleep(50);
							}
							catch (InterruptedException e) 
							{
								e.printStackTrace();
							}
						}
						else
						{
							statusListener.OnStatusCbk(StatusID, id, 0, 0, 0);
						}
					}
				}
				
			}
        }).start();
        
    	
    	
    	
    	
    	
    	
    	
    	
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;

        layout = new LinearLayout(this);
        setContentView(layout);
        
        sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        
        music = sp.load(this, R.raw.camera_click, 1);

        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0,  statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } 
        
        
        layout.setOrientation(LinearLayout.VERTICAL);
        
    	int btn_array_height = (int) (0.08*height);

    	view_top_btn_array = LayoutInflater.from(this).inflate(R.layout.live_view_top_button_array, null);
    	layout.addView(view_top_btn_array, width, btn_array_height);
    
    	
    	vv = new VideoView(this, width, height -btn_array_height-btn_array_height-statusBarHeight );
    	layout.addView(vv, width, height -btn_array_height-btn_array_height-statusBarHeight );
    
    	view_bottom_btn_array = LayoutInflater.from(this).inflate(R.layout.live_view_bottom_button_array, null);
    	layout.addView(view_bottom_btn_array, width, btn_array_height);
        
        
        
    	btn_snap = (Button)findViewById(R.id.btn_snap);
    	btn_snap.setOnClickListener(new ClickEvent());
		btn_audio = (Button)findViewById(R.id.btn_audio);
		btn_audio.setBackgroundResource(R.drawable.d9);
		btn_audio.setOnClickListener(new ClickEvent());
		btn_talk = (Button)findViewById(R.id.btn_talk);
		btn_talk.setBackgroundResource(R.drawable.d8);
		btn_talk.setOnClickListener(new ClickEvent());
		btn_device = (Button)findViewById(R.id.btn_live_view_device);
		btn_device.setOnClickListener(new ClickEvent());
		btn_exit = (Button)findViewById(R.id.btn_live_view_exit);
		btn_exit.setOnClickListener(new ClickEvent());
		
		btn_Record =(Button)findViewById(R.id.btn_Record);
		btn_Record.setOnClickListener(new ClickEvent());
		
		tv_devName = (TextView)findViewById(R.id.tv_live_vew_dev_name);
		
		
		mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
		    public boolean onDoubleTap(MotionEvent e) {
		    	//Log.d("moon", "============Double tap");
		    	
	    		boolean isNeedForceUpdate = false;
	            DisplayMetrics metric = new DisplayMetrics();
	            getWindowManager().getDefaultDisplay().getMetrics(metric);
	            int width = metric.widthPixels;
	            int height = metric.heightPixels;
	            
		    	if( dispMode == 0 )
		    	{
		    		// Enter full screen mode
		    		dispMode = 1;
		    		
		    		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,       
		                       WindowManager.LayoutParams.FLAG_FULLSCREEN);
		            

		        	if( width > height )
		        	{
		        		isNeedForceUpdate = true;
		        	}
		        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		        	
		        	if( isNeedForceUpdate )
		        	{
		        		onConfigurationChanged( getResources().getConfiguration() );
		        	}
		    	}
		    	else // back to normal mode
		    	{
		    		dispMode = 0;
		    		

		    		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		            getWindow().setAttributes(attrs);
		            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		    		

		        	if( width > height )
		        	{
		        		isNeedForceUpdate = true;
		        	}
		        	
		    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); 
		    		
		        	if( isNeedForceUpdate )
		        	{
		        		onConfigurationChanged( getResources().getConfiguration() );
		        	}

		    	}
	            return super.onDoubleTap(e);  
		    } 
		});

		if( !isVVStarted )
		{
			isVVStarted = true;
			vv.start();
			mAudio.start();
			mTalk.start();
		}

    }
    


	public void onConfigurationChanged(Configuration newConfig)
    {
    	super.onConfigurationChanged( newConfig );
    	
    	
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
    	
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0,  statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        } 

    	int btn_array_height = (int) (0.1*height);
    	
    	layout.removeAllViews();
    	
    	if( dispMode == 1 ) // full screen
    	{
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  
        	vv.setVVMetric(width, height);
        	
        	layout.addView(vv, width, height);
    	}
    	else
    	{
	    	vv.setVVMetric(width, height -btn_array_height-btn_array_height-statusBarHeight);
	    	layout.addView(view_top_btn_array, width, btn_array_height);
	    	layout.addView(vv, width, height -btn_array_height-btn_array_height-statusBarHeight );
	    	layout.addView(view_bottom_btn_array, width, btn_array_height);
    	}
    }
    
    public boolean onTouchEvent (MotionEvent event)
    {
    	mGestureDetector.onTouchEvent( event );
    	
    	final int action = event.getAction();
    	
    	if( action == MotionEvent.ACTION_DOWN )
    	{
    		hasTouchMoved = false;
    		startX = event.getX();
    		startY = event.getY();
    		FSApi.ptzStopRun(0);
    	}
    	else if( action == MotionEvent.ACTION_MOVE )
    	{
    		hasTouchMoved = true;
    	}
    	else if( action == MotionEvent.ACTION_UP )
    	{
    		if( hasTouchMoved )
    		{
    			endX = event.getX();
    			endY = event.getY();
    			
    			
    			moveDistanceX = endX - startX;
    			moveDistanceY = endY - startY;
    			if( (Math.abs(moveDistanceX*moveDistanceX)+Math.abs(moveDistanceY*moveDistanceY)) > 50*50 )
    			{
    				angel = Math.atan2(moveDistanceY, moveDistanceX)/3.14159*180+180;
    				ptzHandler.removeCallbacks(ptzStopRunnable);
    				if( (angel>=22.5) && (angel<67.5) )
    				{
    					FSApi.ptzMoveBottomRight(0);
    				}
    				else if( (angel>=67.5) && (angel<112.5) )
    				{
    					FSApi.ptzMoveDown(0);
    				}
    				else if( (angel>=112.5) && (angel<157.5) )
    				{
    					FSApi.ptzMoveBottomLeft(0);
    				}
    				else if( (angel>=157.5) && (angel<202.5) )
    				{
    					FSApi.ptzMoveLeft(0);
    				}
    				else if( (angel>=202.5) && (angel<247.5) )
    				{
    					FSApi.ptzMoveTopLeft(0);
    				}
    				else if( (angel>=247.5) && (angel<292.5) )
    				{
    					FSApi.ptzMoveUp(0);
    				}
    				else if( (angel>=292.5) && (angel<337.5) )
    				{
    					FSApi.ptzMoveTopRight(0);
    				}
    				else if( ((angel>=337.5)&&(angel<360)) || ((angel>=0)&&(angel<22.5)) )
    				{
    					FSApi.ptzMoveRight(0);
    				}
    				
    				ptzHandler.postDelayed(ptzStopRunnable,1000);
    			}
    		}
    	}

    	
    	return true;
    }
    

    
    public boolean onKeyDown(int keyCode,KeyEvent paramKeyEvent)
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		if( dispMode == 1 ) // Full screen
    		{
    			boolean isNeedForceUpdate = false;
    			
	            DisplayMetrics metric = new DisplayMetrics();
	            getWindowManager().getDefaultDisplay().getMetrics(metric);
	            int width = metric.widthPixels;
	            int height = metric.heightPixels;
    			

    			dispMode = 0;
    			
	    		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
	            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
	            getWindow().setAttributes(attrs);
	            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	    		

	        	if( width > height )
	        	{
	        		isNeedForceUpdate = true;
	        	}
	        	
	    		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); 
	    		
	        	if( isNeedForceUpdate )
	        	{
	        		onConfigurationChanged( getResources().getConfiguration() );
	        	}
    		}
    		else
    		{
    			exit();
    		}
    		
    		return true;
    	}
    	else{
    		return super.onKeyDown(keyCode, paramKeyEvent);
    	}
    }
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	isInLiveViewPage = true;

		SharedPreferences sharedata = getSharedPreferences("CONNECT_DEV_INFO", 0);
		
		int devType = sharedata.getInt("DEV_TYPE", 0);
		String devName = sharedata.getString("DEV_NAME", "");
		String ip = sharedata.getString("IP", "");
		int streamType = sharedata.getInt("STREAM_TYPE", 0);
		int webPort = sharedata.getInt("WEB_PORT", 0);
		int mediaPort = sharedata.getInt("MEDIA_PORT", 0);
		String userName = sharedata.getString("USER_NAME", "admin");
		String password = sharedata.getString("PASSWORD", "");
		String uid = sharedata.getString("UID", "");
    	
    	if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					
					if( "".equals(ip) && "".equals(uid) )
					{
						mTalk.stopTalk();
	    				btn_talk.setBackgroundResource(R.drawable.d8);
	    				talkState = 0;
	    				
	    				btn_audio.setBackgroundResource(R.drawable.d9);
	    				audioState = 0;
						FSApi.stopAudioStream(0);
						vv.stopVideoStream();
						
						android.os.SystemClock.sleep(500);
						
						vv.clearScreen();
						
						FSApi.usrLogOut(0);
					}
					
					if( !isNetworkConnected(this) )
					{
						ActivtyUtil.openToast(this, getResources().getString(R.string.live_view_no_network_promote));
					}
					else
					{

						boolean isNeedReconnect = false;
						if( "".equals(uid) )
						{
							if( (!"".equals(ip))&&(!"".equals(userName)) && (webPort>0) )
							{
								isNeedReconnect = true;
							}
						}
						else
						{
							if( (!"".equals(uid)) && (!"".equals(userName)) )
								{
									isNeedReconnect = true;
								}
						}
						
						if( isNeedReconnect )
						{
							mTalk.stopTalk();
		    				btn_talk.setBackgroundResource(R.drawable.d8);
		    				talkState = 0;
		    				
		    				btn_audio.setBackgroundResource(R.drawable.d9);
		    				audioState = 0;
							FSApi.stopAudioStream(0);
							vv.stopVideoStream();
							
							android.os.SystemClock.sleep(500);
							
							vv.clearScreen();
							
							FSApi.usrLogOut(0);
							

							FSApi.usrLogIn(devType, ip, userName, password, streamType, webPort, mediaPort, uid, 0);
							if( uid.length() > 0 )
							{
								tv_devName.setText(devName+"("+uid+")");
							}
							else
							{
								tv_devName.setText(devName+"("+ip+")");
							}
							hasConnected = false;
							
							lastConnectIpcInfoTemp.devType = devType;
							lastConnectIpcInfoTemp.devName = devName;
							lastConnectIpcInfoTemp.streamType = streamType;
							lastConnectIpcInfoTemp.ip = ip;
							lastConnectIpcInfoTemp.webport = webPort;
							lastConnectIpcInfoTemp.mediaport = mediaPort;
							lastConnectIpcInfoTemp.uid = uid;
							lastConnectIpcInfoTemp.userName = userName;
							lastConnectIpcInfoTemp.password = password;
							
							isVideoStreamStarted = true;
						}
						else
						{
							tv_devName.setText("");
						}
					}
				} catch (Exception e) {
					ActivtyUtil.showAlert(LiveView.this, "Error", e.getMessage(),
							getResources().getString(R.string.btn_OK));
				}
			}
	    	else
	    	{
				if( "".equals(ip) && "".equals(uid) )
				{
    				btn_audio.setBackgroundResource(R.drawable.d8);
    				audioState = 0;
					FSApi.stopAudioStream(0);
					vv.stopVideoStream();
					
					android.os.SystemClock.sleep(500);
					
					vv.clearScreen();
					
					FSApi.usrLogOut(0);
					
					tv_devName.setText("");
				}
	    	}
		}
	}

	public boolean  isNetworkConnected(Activity paramActivity)
	{
		boolean i;
		if(paramActivity == null)
			i = false;

		NetworkInfo localNetworkInfo = ((ConnectivityManager)paramActivity.getSystemService("connectivity")).getActiveNetworkInfo();
		if((localNetworkInfo == null)||(!(localNetworkInfo.isAvailable())))
			i = false;
		else
		   i = true;
		
		
		return i;
		
	}


	class ClickEvent implements View.OnClickListener{

    	@Override
    	public void onClick(View v) {
    		if( v == btn_snap )
    		{
    			if( hasConnected )
    			{
	    			sp.play(music, 1, 1, 0, 0, 1);
	    			
	    			String picSaveDir = Environment.getExternalStorageDirectory().getPath()+"/IPC/snap";
	    			File file = new File( picSaveDir );
	    			if( !file.exists() )
	    			{
	    				file.mkdirs();
	    			}
	    			File f=new File(picSaveDir+"/"+System.currentTimeMillis()+".jpg");
	    			FSApi.snapPic(f.getAbsolutePath(),0);
	    			Toast.makeText(LiveView.this, "snap ok ", Toast.LENGTH_SHORT).show();
	    			
    			}
    		}
    		else if(v == btn_audio )
    		{
    			if( hasConnected )
    			{
	    			if( audioState == 0 )
	    			{
	    				btn_audio.setBackgroundResource(R.drawable.d1);
	    				FSApi.startAudioStream(0);
	    				audioState = 1;
	    				ActivtyUtil.openToast(LiveView.this, getResources().getString(R.string.open_audio_promote_success) );
	    			}
	    			else
	    			{
	    				btn_audio.setBackgroundResource(R.drawable.d9);
	    				FSApi.stopAudioStream(0);
	    				audioState = 0;
	    				ActivtyUtil.openToast(LiveView.this, getResources().getString(R.string.close_audio_promote_success) );
	    			}
    			}
    		}
    		else if( v == btn_talk )
    		{
    			if( hasConnected )
    			{
	    			if( talkState == 0 )
	    			{
	    				btn_talk.setBackgroundResource(R.drawable.d2);
	    				mTalk.startTalk(lastConnectIpcInfoTemp.devType);
	    				talkState = 1;
	    			}
	    			else
	    			{
	    				btn_talk.setBackgroundResource(R.drawable.d8);
	    				mTalk.stopTalk();
	    				talkState = 0;
	    			}
    			}
    		}
    		else if(v == btn_device)
    		{
    			Intent mainIntent = new Intent( LiveView.this, DeviceList.class );
    			
    			startActivityForResult(mainIntent, 0);

    			overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
    			
    			isInLiveViewPage = false;
    		}
    		else if(v == btn_Record)
    		{
    			if(!IsRecord)
    			{
    				IsRecord=true;
    				btn_Record.setBackgroundResource(R.drawable.d5);
    				String SDPATH=Environment.getExternalStorageDirectory().toString();
    				String filepath=SDPATH+"/IPC/Video";
    				File file = new File(filepath);
    				if( !file.exists() )
    				{
    					file.mkdirs();
    				}
    				String fileName=System.currentTimeMillis()+".avi";
    				
    				FSApi.StartRecord(filepath+"/", fileName, 0);
    				Toast.makeText(LiveView.this, "start record", Toast.LENGTH_SHORT).show();
    			}
    			else
    			{
    				IsRecord=false;
    				btn_Record.setBackgroundResource(R.drawable.d4);
    				FSApi.StopRecord(0);
    				Toast.makeText(LiveView.this, "stop record", Toast.LENGTH_SHORT).show();
    			}
    		}
    		else if(v == btn_exit)
    		{
    			exit();
    		}
    	}
    }
    
    
    public void exit()
    {
    	new AlertDialog.Builder(this)
    		.setTitle(getResources().getString(R.string.app_exit_title))
    		.setMessage(getResources().getString(R.string.app_exit_warning))
    		.setPositiveButton(getResources().getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
    		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    				
    				vv.stopVideoStream();
    				
    				vv.clearScreen();
    				if(IsRecord)
    				{
    					FSApi.StopRecord(0);
    				}
    				FSApi.usrLogOut(0);
    				
    				vv.stop();
    				mAudio.stop();
    				mTalk.stop();
    				
    				
    				FSApi.Uninit();
    				
    				LiveView.this.finish();
    				isInLiveViewPage = false;
    			}
    		})
    		.setNegativeButton(getResources().getString(R.string.btn_Cancel), new DialogInterface.OnClickListener() {
    		
    			@Override
    			public void onClick(DialogInterface dialog, int which) {
    			dialog.cancel();
    		}
    	})
      .show();
    }
}



class VideoView extends View  implements Runnable{
	
	DecH264 decoder = new DecH264();
	
	AVStreamData videoStreamData = new AVStreamData();
    
    int videoWidth = 640;
    int videoHeight = 480;
    int vvWidth = 0;
    int vvHeight = 0;
    boolean isEnableVideoStream = false;
    
    boolean isThreadRun = true;
    boolean restartDecoder = false;
    

    byte [] mPixel = new byte[1280*720*2];
    int [] gotPicture = new int[4];
    
    ByteBuffer buffer = ByteBuffer.wrap( mPixel );
	Bitmap VideoBit = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565); 
	Bitmap bmpMJ = null;
	int videoFormat = 0; //H264
	
    
    public VideoView(Context context, int width, int height) {
        super(context);
        setFocusable(true);
        
       	int i = 0;
        for(i=0; i<mPixel.length; i++)
        {
        	mPixel[i]=(byte)0x00;
        }
        
		vvWidth = width;
		vvHeight = height;
    }
    
    public void setVVMetric(int width, int height)
    {
		vvWidth = width;
		vvHeight = height;
		
    }
           
    public void start()
    {
    	isThreadRun = true;
    	
    	try{
    		new Thread(this).start();
    	}catch( Exception e )
    	{
    	}
    }
    
    public void stop()
    {
    	isThreadRun = false;
    }
    
    public void startVideoStream()
    {
    	isEnableVideoStream = true;
    	FSApi.startVideoStream(0);
    }
    
    public void stopVideoStream()
    {
    	isEnableVideoStream = false;
    	FSApi.stopVideoStream(0);
    	restartDecoder = true;
    }
    
    public void clearScreen()
    {
       	int i = 0;
       	synchronized(this)
       	{
	        for(i=0; i<mPixel.length; i++)
	        {
	        	mPixel[i]=(byte)0x00;
	        }
       	}
        
        postInvalidate();
    }
    
    
    protected Bitmap getScaleBmp(Bitmap src, float sx, float sy)
    {
  	  	Matrix matrix = new Matrix(); 
  	  	matrix.postScale(sx,sy);
  	  	Bitmap resizeBmp = Bitmap.createBitmap( src, 0, 0, 
			  						src.getWidth(), src.getHeight(), matrix, true );
  	  	return resizeBmp;
    }
    
    protected Bitmap getHorizenBmp(Bitmap src)
    {
  	  	Matrix matrix = new Matrix(); 
  	  	matrix.postScale(vvHeight*1.0f/videoHeight,vvHeight*1.0f/videoHeight);
  	  	Bitmap resizeBmp = Bitmap.createBitmap( src, 0, 0, 
			  						src.getWidth(), src.getHeight(), matrix, true );
  	  	return resizeBmp;
    }
    
    protected Bitmap getVerticalBmp(Bitmap src)
    {
  	  	Matrix matrix = new Matrix(); 
  	  	matrix.postScale(vvWidth*1.0f/videoWidth,vvWidth*1.0f/videoWidth);
  	  	Bitmap resizeBmp = Bitmap.createBitmap( src, 0, 0, 
			  						src.getWidth(), src.getHeight(), matrix, true );
  	  	return resizeBmp;
    }
        
    @Override
    protected void onDraw(Canvas canvas) {
    	{ 
    		super.onDraw(canvas); 
    		
    		if( videoFormat == 0 )
    		{
    			buffer.rewind();
	    		VideoBit.copyPixelsFromBuffer(buffer);
	    		
	    		if( vvWidth > vvHeight )
	    		{
	    			if( vvHeight > videoHeight*(vvWidth*1.0f/videoWidth) )
	    			{
	    				canvas.drawBitmap(getVerticalBmp(VideoBit), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
	    			}
	    			else
	    			{
	    				canvas.drawBitmap(getHorizenBmp(VideoBit), (vvWidth-vvHeight*1.0f/videoHeight*videoWidth)/2, 0, null); 
	    			}
	    		}
	    		else
	    		{
	    			canvas.drawBitmap(getVerticalBmp(VideoBit), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
	    		}
    		}
    		else
    		{
    			if( bmpMJ != null )
    			{
		    		if( vvWidth > vvHeight )
		    		{
		    			if( vvHeight > videoHeight )
		    			{
		    				canvas.drawBitmap(getVerticalBmp(bmpMJ), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
		    			}
		    			else
		    			{
		    				canvas.drawBitmap(getHorizenBmp(bmpMJ), (vvWidth-vvHeight*1.0f/videoHeight*videoWidth)/2, 0, null); 
		    			}
		    		}
		    		else
		    		{
		    			canvas.drawBitmap(getVerticalBmp(bmpMJ), 0, (vvHeight-vvWidth*1.0f/videoWidth*videoHeight)/2, null); 
		    		}
    			}
    		}
    	}
    }
    
    
    public void run()   
    {   
  
    	decoder.InitDecoder();
    	
        while ( isThreadRun )   
        {
        	if( isEnableVideoStream )
        	{
        		try{
        			FSApi.getVideoStreamData( videoStreamData ,0);
        		}catch(Exception e)
        		{
        			continue;
        		}
        		
	       	if( videoStreamData.dataLen > 0)
	        	{
	        		if( videoStreamData.videoFormat == 0 ) // H264
	        		{
	        			videoFormat = 0; 
	        			decoder.DecoderNal( videoStreamData.data, videoStreamData.dataLen, gotPicture, mPixel );
	        		}
	        		else if( videoStreamData.videoFormat == 1 )//MJ
	        		{
	        			videoFormat = 1;
	        			gotPicture[0] = 0;
	        			bmpMJ = BitmapFactory.decodeByteArray(videoStreamData.data, 0, videoStreamData.dataLen);
	        			videoWidth = bmpMJ.getWidth();
	        			videoHeight = bmpMJ.getHeight();
	        			postInvalidate();
	        		}
	        		else
	        		{
	        			gotPicture[0] = 0;
	        		}
	        		
	        	    if( gotPicture[0] > 0 )
	        	    {
	        	    	if( (gotPicture[2]!=videoWidth) || (gotPicture[3]!=videoHeight) )
	        	    	{
	        	    		VideoBit.recycle();
	        	    		videoWidth = gotPicture[2];
	        	    		videoHeight = gotPicture[3];
	        	    		VideoBit = Bitmap.createBitmap(videoWidth, videoHeight, Config.RGB_565); 
	        	    	}
		            	postInvalidate();
	        	    }
	        	}
	        	else
	        	{
	        		try {
	        			Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	}
	        	
	        	if( restartDecoder )
	        	{
	        		decoder.UninitDecoder();
	        		decoder.InitDecoder();
	        		restartDecoder = false;
	        	}
        	}
        	else
        	{
        		try {
        			Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        }
        decoder.UninitDecoder();
    }
}

class Audio implements Runnable{
	
	private AVStreamData audioStreamData = new AVStreamData();
	private  AudioTrack mAudioTrack;
	private boolean hasPlayStart = false;
	private boolean isThreadRun = true;
	
	public void start(){
		
	int  minBufSize = AudioTrack.getMinBufferSize(8000,   
				AudioFormat.CHANNEL_CONFIGURATION_MONO,  
				AudioFormat.ENCODING_PCM_16BIT );  
		
		mAudioTrack =  new  AudioTrack(AudioManager.STREAM_MUSIC,  
                8000,   
                AudioFormat.CHANNEL_CONFIGURATION_MONO,   
                AudioFormat.ENCODING_PCM_16BIT,   
                minBufSize*2,  
                AudioTrack.MODE_STREAM);
		
		mAudioTrack.play();
		
		hasPlayStart = true;
		
		new Thread(this).start();
	}
	
    public void stop()
    {
    	if( hasPlayStart )
    	{
    		hasPlayStart = false;
    		mAudioTrack.stop();
    	}
    	
    	isThreadRun = false;
    }
	
	public void run(){
		
		while ( isThreadRun )
		{
			// Play
			try{
				FSApi.getAudioStreamData( audioStreamData ,0);
    		}catch(Exception e)
    		{
    			continue;
    		}
			if( audioStreamData.dataLen > 0 )
			{
				try{
					mAudioTrack.write(audioStreamData.data, 0, audioStreamData.dataLen );
				} catch  (Exception e) {  
				}
			}
        	else
        	{
        		try {
        			Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
		}
	}
}

class Talk implements Runnable{
	
	private  AudioRecord mAudioRecord;
	private boolean hasRecordStart = false;
	private boolean isThreadRun = true;
	private byte []buffer = new byte[960];
	private int bytesRead = 0;
	private int sendTalk = 0;
	private int deviceType = 1;
	
	public void start(){
		new Thread(this).start();
	}
	
    public void stop()
    {
    	if( hasRecordStart )
    	{
    		hasRecordStart = false;
    		mAudioRecord.stop();
			mAudioRecord.release();
			mAudioRecord = null;
    	}
    	
    	isThreadRun = false;
    }
    
    public void startTalk(int devType)
    {
		int minBufSize = AudioRecord.getMinBufferSize(8000, 
				AudioFormat.CHANNEL_IN_DEFAULT, 
				AudioFormat.ENCODING_PCM_16BIT );
		
		
		mAudioRecord =  new  AudioRecord(MediaRecorder.AudioSource.MIC,  
                8000,   
                AudioFormat.CHANNEL_IN_DEFAULT,   
                AudioFormat.ENCODING_PCM_16BIT,   
                minBufSize );
		
		try
		{
			mAudioRecord.startRecording();
		}catch(Exception e)
		{
		}
		
		hasRecordStart = true;
		deviceType = devType;
		
    	FSApi.startTalk(0);
    	sendTalk = 1;
    }
    
    public void stopTalk()
    {
    	FSApi.stopTalk(0);
    	if( hasRecordStart )
    	{
    		hasRecordStart = false;
    		try
    		{
    			mAudioRecord.stop();
    			mAudioRecord.release();
    			mAudioRecord = null;
    		}catch(Exception e)
    		{
    		}
    	}
    	sendTalk = 0;
    }
	
	public void run(){
		
		int bufLen = 960;
		
		while ( isThreadRun )
		{
			// Record
			if( sendTalk == 1 )
			{
				if( deviceType == 0 ) //MJ
				{
					bufLen = 640; 
				}
				else if( deviceType == 1 ) // H264 
				{
					bufLen = 960;
				}
				else
				{
					bufLen = 960;
				}
				
				try{
					bytesRead = mAudioRecord.read(buffer, 0, bufLen);
					if( bytesRead > 0 )
					{
						FSApi.sendTalkFrame(buffer, bufLen,0);
					}

				} catch  (Exception e) {  
				}
			}
			else
			{
	    		try {
	    			Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
