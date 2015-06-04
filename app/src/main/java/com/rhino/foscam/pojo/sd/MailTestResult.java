package com.rhino.foscam.pojo.sd;

import java.util.HashMap;

public class MailTestResult {
	
	private String result;
	private boolean success;
	private int code;
	
	public MailTestResult(String response) {
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
		 		 result = "Rejected by the sender";
		 		 break;
		case -7: success = false;
		 		 result = "Rejcted by the receiver";
		 		 break;
		case -8: success = false;
		 		 result = "The text rejected";
		 		 break;
		case -9: success = false;
		 		 result = "Authentication not accepted";
		 		 break;
		case -10: success = false;
		 		  result = "Internal Error";
		 		  break;
		case -11: success = false;
		 		  result = "The device does not support TLS/STARTTLS protocol";
		 		  break;
		case -12: success = false;
		 		  result = "Invalid parameter";
		 		  break;
		case -13: success = false;
		 		  result = "The server does not support TLS/STARTTLS protocol";
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
