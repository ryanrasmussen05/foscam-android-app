package com.foscam.ipc;




import java.util.ArrayList;
import java.util.List;


import com.rhino.foscam.R;
import com.ipc.sdk.DevInfo;
import com.ipc.sdk.FSApi;
import com.remote.util.ActivtyUtil;
import com.remote.util.DatabaseHelper;
import com.remote.util.IPCameraInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceList extends Activity{
	
	private ListView device_list;
	private Button btn_add;
	private Button btn_back;
	private Button btn_search;
	private DevInfo[] deviceSearched;
	
	private ProgressDialog progressDialog = null;
	
	
	private List<String> list_item = new ArrayList<String>();
	private List<String> equipment_name_list = new ArrayList<String>();
	private Cursor cursor;
	
	
	private ListView lv;
	private List<String> equipmentID = new ArrayList<String>();
	IPCameraInfo ipcInfo;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.device_list);
		
	    findview();
	    
	    try {
			fillDataToCursor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setListenner();
		
		this.registerForContextMenu( device_list );
	}
	
	public void findview()
	{
		device_list = (ListView)findViewById(R.id.ls_device_list);
		btn_add = (Button)findViewById(R.id.btn_device_list_add);
		btn_back  = (Button)findViewById(R.id.btn_device_list_back);
		btn_search = (Button)findViewById(R.id.btn_device_list_search);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0||requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				try {
					fillDataToCursor();
				} catch (Exception e) {
					ActivtyUtil.showAlert(DeviceList.this, "Error", e.getMessage(),
							getResources().getString(R.string.btn_OK));
				}
			}
		}
	}
	
	final int MENU_EDIT_DEVICE =1;
	final int MENU_DEL_DEVICE =2;

	
	private void modifyequipmentData(int id)
	{
		try {
			equipmentID = getEquipmentIds();
		    int equipment_id = Integer.parseInt(equipmentID.get(id).toString());
			Intent intent = new Intent(DeviceList.this, IPCameraInfoConfig.class);
			intent.putExtra("id", equipment_id);
			startActivityForResult(intent, 1);
			
		} catch (Exception e) {
			Log.d("moon", e.getMessage() );
			//ActivtyUtil.showAlert(DeviceList.this, "Error", e.getMessage(), getResources().getString(R.string.OK));
		}
	}
	
	private void deleteDevice(int id) throws Exception
	{
		try 
		{
			equipmentID =getEquipmentIds();
			final int equipment_id = Integer.parseInt(equipmentID.get(id).toString());
	
		
		   try {
			   equipment_name_list = getData();
			   
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
			String deviceName = equipment_name_list.get(id).toString();
			
			new AlertDialog.Builder(DeviceList.this).setTitle(getResources().getString(R.string.device_list_device_del_title))
			.setMessage(getResources().getString(R.string.device_list_device_del_warning) + deviceName + "?")
			.setPositiveButton(getResources().getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					try {
						
						
						IPCameraInfo info = DatabaseHelper.query(DeviceList.this, equipment_id);
						
						SharedPreferences sharedPreferences = getSharedPreferences("CONNECT_DEV_INFO", 0);
						SharedPreferences.Editor editor = sharedPreferences.edit();
						String ip = sharedPreferences.getString("IP", "");
						String uid = sharedPreferences.getString("UID", "");
						
						if( ((!"".equals(uid)) && uid.equals(info.uid)) ||
							((!"".equals(ip)) && ip.equals(info.ip)) )
						{
							editor.putInt("DEV_TYPE", 0);
							editor.putString("DEV_NAME", "");
							editor.putString("IP", "");
							editor.putInt("STREAM_TYPE", 1);
							editor.putInt("WEB_PORT", 0);
							editor.putInt("MEDIA_PORT", 0);
							editor.putString("USER_NAME", "");
							editor.putString("PASSWORD", "");
							editor.putString("UID", "");
							editor.commit();
						}

						DatabaseHelper.delete(DeviceList.this, equipment_id);
						fillDataToCursor();
						ActivtyUtil.openToast(DeviceList.this,
									getResources().getString(R.string.device_list_device_del_success));
					} catch (Exception e) {
						//Log.e(TAG, e.getMessage(), e);
						ActivtyUtil.openToast(DeviceList.this, e.getMessage());
					}
				}
			})
			.setNegativeButton(getResources().getString(R.string.btn_Cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int which) {
	
						}
					}).show();
			}catch (Exception e) {
				ActivtyUtil.showAlert(DeviceList.this, "Error", e.getMessage(), getResources().getString(R.string.btn_OK));
		}
	}
	
	public void clearDeviceList()
	{
		try {
			DatabaseHelper.deleteAll(DeviceList.this);
			fillDataToCursor();
		} catch (Exception e) {
			//Log.e(TAG, e.getMessage(), e);
			ActivtyUtil.openToast(DeviceList.this, e.getMessage());
		}
		
		SharedPreferences sharedPreferences = getSharedPreferences("CONNECT_DEV_INFO", 0);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("DEV_TYPE", 0);
		editor.putString("DEV_NAME", "");
		editor.putString("IP", "");
		editor.putInt("STREAM_TYPE", 1);
		editor.putInt("WEB_PORT", 0);
		editor.putInt("MEDIA_PORT", 0);
		editor.putString("USER_NAME", "");
		editor.putString("PASSWORD", "");
		editor.putString("UID", "");
		editor.commit();
		
		Log.d("moon", "clear connect info");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int id = (int) info.id;
		
		Log.d("remote", "id = "  + id);
		     
		switch(item.getItemId())
		{
		case MENU_EDIT_DEVICE:
			modifyequipmentData(id);
			break;
		case MENU_DEL_DEVICE:
			try {
				deleteDevice(id);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("moon", e.getMessage() );
			}
			break;
		
		}
		return super.onContextItemSelected(item);
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		
		menu.add(0, MENU_EDIT_DEVICE, 0, getResources().getString(R.string.device_list_context_menu_edit));
		menu.add(0, MENU_DEL_DEVICE, 0, getResources().getString(R.string.device_list_context_menu_del));

		
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
    // Menu item Ids
    public static final int MENU_ID_CLEAR_LIST = Menu.FIRST + 10;    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, MENU_ID_CLEAR_LIST, 0, getResources().getString(R.string.device_list_menu_clear_device_list));   

        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {        
	        case MENU_ID_CLEAR_LIST:
	        {
	        	clearDeviceList();
	            return true;
	        }
        }
        return super.onOptionsItemSelected(item);
    }
	
	
	private void fillDataToCursor() throws Exception
	{
		// Log.d("moon","fillDataToCursor" );

		 device_list.setAdapter( new ArrayAdapter<String>(this, R.layout.list_item1, getData()) );
		 device_list.refreshDrawableState();
		 
		 
	}
	
	
	private void setListenner() {
		device_list.setOnItemClickListener(itemClickListener);
		btn_add.setOnClickListener(btnAddListener);

		btn_back.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btn_search.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FSApi.searchDev();
				
				progressDialog = ProgressDialog.show(DeviceList.this, getResources().getString(R.string.device_list_search_title),  getResources().getString(R.string.device_list_search_wait), true);	
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						getDeviceList();
						progressDialog.dismiss();
					}
				},3000);
			}
		});
		
	}
	
	private void getDeviceList()
	{
		deviceSearched = FSApi.getDevList();
		
		String[] devices = new String[deviceSearched.length];
		boolean[] status = new boolean[deviceSearched.length];
		
		if( deviceSearched.length > 0 )
		{
			int i = 0;
			for( i = 0; i < deviceSearched.length; i++ )
			{
				if( !"".equals(deviceSearched[i].uid))
				{
					devices[i] = deviceSearched[i].devName+"("+deviceSearched[i].uid+")";
				}
				else
				{
					devices[i] = deviceSearched[i].devName+"("+deviceSearched[i].ip+")";
				}
				status[i] = false;
			}
			
			AlertDialog builder = new AlertDialog.Builder(this)
	        .setTitle(getResources().getString(R.string.device_list_sel_promote))
	        .setMultiChoiceItems( 	devices, status,
					                new OnMultiChoiceClickListener()
					                {
					                    @Override
					                    public void onClick(DialogInterface dialog,
					                            int which, boolean isChecked)
					                    {
					                       // TODO Auto-generated method stub
					                    }
					                }  
	        					)
	        .setPositiveButton(getResources().getString(R.string.btn_OK), new DialogInterface.OnClickListener()
	        {
	            @Override
	            public void onClick(DialogInterface dialog, int which)
	            {
	         	   int existingCnt = 0;
	               for (int i = 0; i < deviceSearched.length; i++)
	               {
	                   if ( lv.getCheckedItemPositions().get(i) )
	                   {
	                	   try{
	                		   Cursor cursour;
	                		   if( "".equals(deviceSearched[i].uid) )
	                		   {
	                			   cursour = DatabaseHelper.QueryDevice( DeviceList.this, deviceSearched[i].ip, deviceSearched[i].webPort );
	                		   }
	                		   else
	                		   {
	                			   cursour = DatabaseHelper.QueryDevice( DeviceList.this, deviceSearched[i].uid );
	                		   }
	                		   existingCnt = cursour.getCount();
	                		   cursour.close();
	                	   }catch(Exception e)
	                	   {
	                		   Log.d("moon", e.getMessage());
	                	   }

	                	   if( existingCnt == 0 )
	                	   {
		                	   ContentValues contentValue = new ContentValues();
		                    
		                	   contentValue.put("devType", deviceSearched[i].devType);
		                	   contentValue.put("devName", deviceSearched[i].devName);
		                	   contentValue.put("ip", deviceSearched[i].ip);
		                	   contentValue.put("streamType", 0);
		                	   contentValue.put("webPort", deviceSearched[i].webPort);
		                	   contentValue.put("mediaPort", deviceSearched[i].mediaPort);
		                	   contentValue.put("uid", deviceSearched[i].uid);
		                	   contentValue.put("reserve1", 0);
		                	   contentValue.put("reserve2", 0);
		                	   contentValue.put("reserve3", 0);
		                	   contentValue.put("reserve4", 0);
		  					
		
		  						try {
									DatabaseHelper.insert(DeviceList.this, "tb_device_list", contentValue);
									fillDataToCursor();
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
	                	   }
	                   }
	               }
	            }
	        }).setNegativeButton(getResources().getString(R.string.btn_Cancel), null).create();
			
			lv = builder.getListView();
			builder.show();
		}
		else
		{
			// No device found
			ActivtyUtil.showAlert(DeviceList.this, getResources().getString(R.string.device_list_search_no_dev_found_title), 
					getResources().getString(R.string.device_list_search_no_dev_found), 
					getResources().getString(R.string.btn_OK));
		}
	}
	

	
	
	private View.OnClickListener btnAddListener = new View.OnClickListener() {

		public void onClick(View v) {
			Intent intent = new Intent(DeviceList.this, IPCameraInfoConfig.class);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		}

	};
 /*   public boolean onKeyDown(int paramInt,KeyEvent paramKeyEvent)
    {
    	if(paramInt == 4)
    		exit();
    	
    	return super.onKeyDown(paramInt, paramKeyEvent);
    	
    }*/
	
	
	 private List<String> getData() throws Exception{
		 List<String> data = new ArrayList<String>();
		 DatabaseHelper helper = new DatabaseHelper(this);
        
		 cursor = helper.loadAllName();
		 int count = cursor.getCount();

		 Log.d("moon","tb_device_list record cnt:" + count);
		 if(count ==0 )
			 return data;
        
		 cursor.moveToFirst();
        
		 String uid = cursor.getString(3);
		 String ip = cursor.getString(2);
		 if( "".equals(uid) )
		 {
			 data.add( cursor.getString(1)+"("+ip+")" ); // DevName(IP Addr)
		 }
		 else
		 {
			 data.add( cursor.getString(1)+"("+uid+")" ); // DevName(UID)
		 }
	
		 while(cursor.moveToNext()){
			 if( "".equals(cursor.getString(3)))
			 {
				 data.add( cursor.getString(1)+"("+cursor.getString(2)+")" );
			 }
			 else
			 {
				 data.add( cursor.getString(1)+"("+cursor.getString(3)+")" );
			 }
 
			// Log.d("moon","cursor.getString(1) =" +cursor.getString(1) );
		 }
   
        
		 cursor.close();
		 helper.close();
		 
		 return data;
	 }
	 
	 private List<String> getEquipmentIds() throws Exception{
		 
		    List<String> data = new ArrayList<String>();
	        DatabaseHelper helper = new DatabaseHelper(this);
	        cursor = helper.loadAllName();
	        int count = cursor.getCount();
	       // Log.d("remote","getData --count  =" + count);
	        if(count ==0 )
	        	return data;
	        
	        cursor.moveToFirst();
	        
	        data.add(cursor.getString(0));

	       // Log.d("remote","cursor.getString(0) =" +cursor.getString(0));
	     
	        while(cursor.moveToNext()){
	        	data.add(cursor.getString(0));
	  
	        	//Log.d("remote","cursor.getString(0) =" +cursor.getString(0));

			}
	           
	        cursor.close();
	    	helper.close();
	        return data;
	    }
	 

	 OnItemClickListener itemClickListener = new OnItemClickListener() {
		 
		 @Override
		 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		     try {
				 list_item = getData();
			 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
			
			 try {
				 equipmentID = getEquipmentIds();
			 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
			
			 final int equipment_id = Integer.parseInt(equipmentID.get(position).toString());
			 
			 try{
			 	ipcInfo = DatabaseHelper.query(DeviceList.this, equipment_id);
			 	if( ipcInfo.devName == null ){
			 		ipcInfo.devName = "";
			 	}
			 	if( ipcInfo.ip == null ){
			 		ipcInfo.ip = "";
			 	}
			 	if( ipcInfo.userName == null ){
			 		ipcInfo.userName = "";
			 	}
			 	if( ipcInfo.password == null ){
			 		ipcInfo.password = "";
			 	}
			 	if( ipcInfo.uid == null ){
			 		ipcInfo.uid = "";
			 	}
			 	
			 	if( ((ipcInfo.ip==null)||("".equals(ipcInfo.ip))) && 
			 		((ipcInfo.uid==null)||("".equals(ipcInfo.uid))) )
			 	{
			 	      new AlertDialog.Builder(DeviceList.this)
			 	      .setTitle(getResources().getString(R.string.device_list_device_dev_config_error_title))
			 	      .setMessage(getResources().getString(R.string.device_list_device_dev_config_error_warning))
			 	      .setCancelable(false)
			 	      .setPositiveButton(getResources().getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
			 	    		@Override
			 	    		public void onClick(DialogInterface dialog, int which) {
			 	    			// TODO Auto-generated method stub
			 	    			dialog.cancel();
			 	    		}
			 	    	})
			 	      .show();
			 	}
			 	else if( (ipcInfo.userName==null) || ("".equals(ipcInfo.userName)) )
			 	{
			 		LayoutInflater inflater = getLayoutInflater();
			 		final View enter_usr_pwd = inflater.inflate(R.layout.user_name_pwd_enter,null); 
			 		        
			 	    new AlertDialog.Builder(DeviceList.this)
			 	      .setTitle(getResources().getString(R.string.device_list_device_user_name_empty_title))
			 	      .setView( enter_usr_pwd )
			 	      .setPositiveButton(getResources().getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
			 	    		@Override
			 	    		public void onClick(DialogInterface dialog, int which) {
			 	    			// TODO Auto-generated method stub
			 	    			
			 	    			try
			 	    			{
				 	    			EditText ed_userName = (EditText)enter_usr_pwd.findViewById(R.id.ed_enter_usr_pwd_user);
				 	    			EditText ed_password = (EditText)enter_usr_pwd.findViewById(R.id.ed_enter_usr_pwd_pwd);
				 	    			
				 	    			String userName = ed_userName.getText().toString();
				 	    			String password = ed_password.getText().toString();

			 	    			
				 	    			if( "".equals(userName) )
				 	    			{
				 	    				Toast.makeText(DeviceList.this, "User name empty", Toast.LENGTH_LONG).show();
				 	    			}
				 	    			else
				 	    			{
				 	    				try {
				 	    					ContentValues contentValue = new ContentValues();
				 	    					
				 	    					contentValue.put("devType", ipcInfo.devType);
				 	    					contentValue.put("devName", ipcInfo.devName);
				 	    					contentValue.put("ip", ipcInfo.ip);
				 	    					contentValue.put("streamType",ipcInfo.streamType);
				 	    					contentValue.put("webPort",ipcInfo.webport);
				 	    					contentValue.put("mediaPort",ipcInfo.mediaport);
				 	    					contentValue.put("uid", ipcInfo.uid);
				 	    					contentValue.put("userName", userName.trim());
				 	    					contentValue.put("password", password.trim());
				 	    					
				 	    					DatabaseHelper.update(DeviceList.this, "tb_device_list", contentValue, equipment_id);
				 	    				} catch (Exception e) {
				 	    					Log.e("moon", e.getMessage(),e);
				 	    				}

				 						SharedPreferences sharedPreferences = getSharedPreferences("CONNECT_DEV_INFO", 0);
				 						SharedPreferences.Editor editor = sharedPreferences.edit();
				 						editor.putInt("DEV_TYPE", ipcInfo.devType);
				 						editor.putString("DEV_NAME", ipcInfo.devName);
				 						editor.putString("IP", ipcInfo.ip);
				 						editor.putInt("STREAM_TYPE", ipcInfo.streamType);
				 						editor.putInt("WEB_PORT", ipcInfo.webport);
				 						editor.putInt("MEDIA_PORT", ipcInfo.mediaport);
				 						editor.putString("USER_NAME", userName);
				 						editor.putString("PASSWORD", password);
				 						editor.putString("UID", ipcInfo.uid);
				 					  	editor.commit();
				 					  	
				 					  	dialog.dismiss();

				 					  	setResult(Activity.RESULT_OK);
				 							
				 						DeviceList.this.finish();
				 						
				 						overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
				 	    			}
			 	    			
			 	    			}catch( Exception e )
			 	    			{
			 	    				Log.d("moon", e.getMessage() );
			 	    			}
			 	    		}
			 	    	})
			 	      .setNegativeButton(getResources().getString(R.string.btn_Cancel), new DialogInterface.OnClickListener() {
    		
			 	    	  @Override
			 	    	  public void onClick(DialogInterface dialog, int which) {
			 	    		  // TODO Auto-generated method stub
			 	    		  dialog.cancel();
			 	    	  }
			 	      })
			 	      .show();
			 	}
			 	else
			 	{
					SharedPreferences sharedPreferences = getSharedPreferences("CONNECT_DEV_INFO", 0);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putInt("DEV_TYPE", ipcInfo.devType);
					editor.putString("DEV_NAME", ipcInfo.devName);
					editor.putString("IP", ipcInfo.ip);
					editor.putInt("STREAM_TYPE", ipcInfo.streamType);
					editor.putInt("WEB_PORT", ipcInfo.webport);
					editor.putInt("MEDIA_PORT", ipcInfo.mediaport);
					editor.putString("USER_NAME", ipcInfo.userName);
					editor.putString("PASSWORD", ipcInfo.password);
					editor.putString("UID", ipcInfo.uid);
					
					Log.d("moon", "Save devType:"+ipcInfo.devType);
					Log.d("moon", "Save devName:"+ipcInfo.devName);
					Log.d("moon", "Save ip:"+ipcInfo.ip);
					Log.d("moon", "Save streamType:"+ipcInfo.streamType);
					Log.d("moon", "Save webPort:"+ipcInfo.webport);
					Log.d("moon", "Save mediaPort:"+ipcInfo.mediaport);
					Log.d("moon", "Save userName:"+ipcInfo.userName);
					Log.d("moon", "Save password:"+ipcInfo.password);
					Log.d("moon", "Save uid:"+ipcInfo.uid);
				  	
				  	editor.commit();
				  	
				  	setResult(Activity.RESULT_OK);
						
					DeviceList.this.finish();
					
					overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
			 	}
			  	 
			 }catch( Exception e )
			 {
				 Log.d("moon", e.getMessage() );
			 }
		}
	};
}
