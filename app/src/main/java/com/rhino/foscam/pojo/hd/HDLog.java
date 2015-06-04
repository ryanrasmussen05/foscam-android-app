package com.rhino.foscam.pojo.hd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HDLog {
	
	private boolean complete = false;
	private StringBuilder log = new StringBuilder();;
	
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public void updateLog(String log) {
		String[] results = log.split("\\+");
		
		Date date = new Date((Long.parseLong(results[0])) * 1000);
		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy h:mm a");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		String formattedDate = sdf.format(date.getTime());
		
		int ipInt = (int) Long.parseLong(results[2]);
		int ip4 = ipInt >>> 24;
		int ip3 = (ipInt << 8) >>> 24;
		int ip2 = (ipInt << 16) >>> 24;
		int ip1 = (ipInt << 24) >>> 24;
		String ipString = ip1 + "." + ip2 + "." + ip3 + "." + ip4;
		
		this.log.append(formattedDate + " - " + results[1] + " - " + ipString + " - " + decodeAction(results[3]) + System.getProperty("line.separator"));
	}
	
	public String getText() {
		return log.toString();
	}
	
	private String decodeAction(String action) {
		if(action.equals("0")) {
			return "System Power On";
		} else if(action.equals("1")) {
			return "Motion Alarm";
		} else if(action.equals("2")) {
			return "User Login";
		} else if(action.equals("3")) {
			return "User Login";
		} else if(action.equals("4")) {
			return "User Logout";
		} else {
			return "Unknown Action";
		}
	}

}
