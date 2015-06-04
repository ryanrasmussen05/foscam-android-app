package com.rhino.foscam.pojo.sd;

import java.util.HashMap;

public class FTPTestResult {
	
	private String result;
	private boolean success;
	private int code;
	
	public FTPTestResult(String response) {
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
		
		this.code = Integer.parseInt(map.get("result"));
		
		switch(code) {
		case 0: success = true;
				break;
		case -1: success = false;
				 result = "Cannot connect to server";
				 break;
		case -2: success = false;
				 result = "Network error";
				 break;
		case -3: success = false;
		 		 result = "Server error";
		 		 break;
		case -4: success = false;
		 		 result = "Incorrect User";
		 		 break;
		case -5: success = false;
		 		 result = "Incorrect Password";
		 		 break;
		case -6: success = false;
		 		 result = "Incorrect Catalogue";
		 		 break;
		case -7: success = false;
		 		 result = "Pasv Mode error";
		 		 break;
		case -8: success = false;
		 		 result = "Port Mode error";
		 		 break;
		case -9: success = false;
		 		 result = "Store Command error";
		 		 break;
		default: success = false;
		 		 result = "Unknown error";
		 		 break;		 
		}
		
	}
	
	public String getResult() {
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

}
