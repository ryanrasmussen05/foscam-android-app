package com.rhino.foscam.pojo.sd;

import java.util.HashMap;

public class CameraStatus {
	
	private String alias;
	private boolean alarm;
	
	public CameraStatus(String response) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		String[] params = response.split(";");
		
		for(String param : params) {
			
			String key = "";
			String value = "";
			
			String[] details = param.split("=");
			key = details[0].split(" ")[1];
			value = details[1];
			
			if(value.startsWith("'") && value.endsWith("'")) {
				value = value.substring(1, value.length() - 1);
			}
			
			map.put(key, value);
		}
		
		this.alias = map.get("alias");
		
		String alarmString = map.get("alarm_status");
		if(alarmString.equals("0")) {
			this.alarm = false;
		} else {
			this.alarm = true;
		}
	}
	
	public String getAlias() {
		return alias;
	}

	public boolean isAlarmActive() {
		return alarm;
	}

}
