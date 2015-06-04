package com.rhino.foscam.pojo.sd;

import java.util.HashMap;

public class CameraParams {
	
	private String alias;
	private boolean motion;
	private int motionSensitivity;
	private boolean motionCompensation;
	private boolean sound;
	private int soundSensitivity;
	private long epoch;
	private int dst;
	private boolean ntpEnable;
	private String ntpServer;
	private int timeZone;
	private String user1Name;
	private String user1Password;
	private int user1Priv;
	private String user2Name;
	private String user2Password;
	private int user2Priv;
	private String user3Name;
	private String user3Password;
	private int user3Priv;
	private String user4Name;
	private String user4Password;
	private int user4Priv;
	private String user5Name;
	private String user5Password;
	private int user5Priv;
	private String user6Name;
	private String user6Password;
	private int user6Priv;
	private String user7Name;
	private String user7Password;
	private int user7Priv;
	private String user8Name;
	private String user8Password;
	private int user8Priv;
	private String ftpServer;
	private String ftpPort;
	private String ftpUser;
	private String ftpPassword;
	private String ftpFolder;
	private int ftpMode;
	private String ftpUploadInterval;
	private String ftpFilename;
	private String mailServer;
	private String mailPort;
	private int mailMode;
	private String mailUser;
	private String mailPassword;
	private String mailSender;
	private String mailReceiver1;
	private String mailReceiver2;
	private String mailReceiver3;
	private String mailReceiver4;
	private boolean mailReportIP;
	private boolean mailOnAlarm;
	private int uploadOnAlarm;
	private boolean alarmScheduleEnabled;
	
	
	public CameraParams() {}
	
	public CameraParams(String response) {
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
		
		String soundAlarm = map.get("alarm_sounddetect_armed");
		this.sound = soundAlarm.equals("1");
		
		this.soundSensitivity = Integer.parseInt(map.get("alarm_sounddetect_sensitivity"));
		
		String motionAlarm = map.get("alarm_motion_armed");
		this.motion = motionAlarm.equals("1");
		
		this.motionSensitivity = Integer.parseInt(map.get("alarm_motion_sensitivity"));
		
		String motionComp = map.get("alarm_motion_compensation");
		this.motionCompensation = motionComp.equals("1");
		
		String epochString = map.get("now");
		this.epoch = Long.parseLong(epochString);
		
		String dstString = map.get("daylight_saving_time");
		this.dst = Integer.parseInt(dstString);
		
		String ntpEnableString = map.get("ntp_enable");
		this.ntpEnable = ntpEnableString.equals("1");
		
		this.ntpServer = map.get("ntp_svr");
		
		String timeZoneString = map.get("tz");
		this.timeZone = Integer.parseInt(timeZoneString);
		
		this.user1Name = map.get("user1_name");
		this.user1Password = map.get("user1_pwd");
		this.user1Priv = Integer.parseInt(map.get("user1_pri"));
		
		this.user2Name = map.get("user2_name");
		this.user2Password = map.get("user2_pwd");
		this.user2Priv = Integer.parseInt(map.get("user2_pri"));
		
		this.user3Name = map.get("user3_name");
		this.user3Password = map.get("user3_pwd");
		this.user3Priv = Integer.parseInt(map.get("user3_pri"));
		
		this.user4Name = map.get("user4_name");
		this.user4Password = map.get("user4_pwd");
		this.user4Priv = Integer.parseInt(map.get("user4_pri"));
		
		this.user5Name = map.get("user5_name");
		this.user5Password = map.get("user5_pwd");
		this.user5Priv = Integer.parseInt(map.get("user5_pri"));
		
		this.user6Name = map.get("user6_name");
		this.user6Password = map.get("user6_pwd");
		this.user6Priv = Integer.parseInt(map.get("user6_pri"));
		
		this.user7Name = map.get("user7_name");
		this.user7Password = map.get("user7_pwd");
		this.user7Priv = Integer.parseInt(map.get("user7_pri"));
		
		this.user8Name = map.get("user8_name");
		this.user8Password = map.get("user8_pwd");
		this.user8Priv = Integer.parseInt(map.get("user8_pri"));
		
		this.ftpServer = map.get("ftp_svr");
		this.ftpPort = map.get("ftp_port");
		this.ftpUser = map.get("ftp_user");
		this.ftpPassword = map.get("ftp_pwd");
		this.ftpFolder = map.get("ftp_dir");
		this.ftpMode = Integer.parseInt(map.get("ftp_mode"));
		this.ftpUploadInterval = map.get("ftp_upload_interval");
		this.ftpFilename = map.get("ftp_filename");
		
		this.mailServer = map.get("mail_svr");
		this.mailPort = map.get("mail_port");
		this.mailMode = Integer.parseInt(map.get("mail_tls"));
		this.mailUser = map.get("mail_user");
		this.mailPassword = map.get("mail_pwd");
		this.mailSender = map.get("mail_sender");
		this.mailReceiver1 = map.get("mail_receiver1");
		this.mailReceiver2 = map.get("mail_receiver2");
		this.mailReceiver3 = map.get("mail_receiver3");
		this.mailReceiver4 = map.get("mail_receiver4");
		String reportIP = map.get("mail_inet_ip");
		this.mailReportIP = reportIP.equals("1");
		
		String mailAlarm = map.get("alarm_mail");
		this.mailOnAlarm = mailAlarm.equals("1");
		
		String sched = map.get("alarm_schedule_enable");
		this.alarmScheduleEnabled = sched.equals("1");
		
		this.uploadOnAlarm = Integer.parseInt(map.get("alarm_upload_interval"));
	}

	public String getAlias() {
		return alias;
	}

	public boolean isMotionAlarmEnabled() {
		return motion;
	}

	public boolean isSoundAlarmEnabled() {
		return sound;
	}
	
	public void setMotionAlarmEnabled(boolean enable) {
		this.motion = enable;
	}
	
	public void setSoundAlarmEnabled(boolean enable) {
		this.sound = enable;
	}

	public long getEpoch() {
		return epoch;
	}

	public int getDst() {
		return dst;
	}

	public boolean isNtpEnabled() {
		return ntpEnable;
	}

	public String getNtpServer() {
		return ntpServer;
	}

	public int getTimeZone() {
		return timeZone;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setEpoch(long epoch) {
		this.epoch = epoch;
	}

	public void setDst(int dst) {
		this.dst = dst;
	}

	public void setNtpEnable(boolean ntpEnable) {
		this.ntpEnable = ntpEnable;
	}

	public void setNtpServer(String ntpServer) {
		this.ntpServer = ntpServer;
	}

	public void setTimeZone(int timeZone) {
		this.timeZone = timeZone;
	}

	public String getUser1Name() {
		return user1Name;
	}

	public void setUser1Name(String user1Name) {
		this.user1Name = user1Name;
	}

	public String getUser1Password() {
		return user1Password;
	}

	public void setUser1Password(String user1Password) {
		this.user1Password = user1Password;
	}

	public int getUser1Priv() {
		return user1Priv;
	}

	public void setUser1Priv(int user1Priv) {
		this.user1Priv = user1Priv;
	}

	public String getUser2Name() {
		return user2Name;
	}

	public void setUser2Name(String user2Name) {
		this.user2Name = user2Name;
	}

	public String getUser2Password() {
		return user2Password;
	}

	public void setUser2Password(String user2Password) {
		this.user2Password = user2Password;
	}

	public int getUser2Priv() {
		return user2Priv;
	}

	public void setUser2Priv(int user2Priv) {
		this.user2Priv = user2Priv;
	}

	public String getUser3Name() {
		return user3Name;
	}

	public void setUser3Name(String user3Name) {
		this.user3Name = user3Name;
	}

	public String getUser3Password() {
		return user3Password;
	}

	public void setUser3Password(String user3Password) {
		this.user3Password = user3Password;
	}

	public int getUser3Priv() {
		return user3Priv;
	}

	public void setUser3Priv(int user3Priv) {
		this.user3Priv = user3Priv;
	}

	public String getUser4Name() {
		return user4Name;
	}

	public void setUser4Name(String user4Name) {
		this.user4Name = user4Name;
	}

	public String getUser4Password() {
		return user4Password;
	}

	public void setUser4Password(String user4Password) {
		this.user4Password = user4Password;
	}

	public int getUser4Priv() {
		return user4Priv;
	}

	public void setUser4Priv(int user4Priv) {
		this.user4Priv = user4Priv;
	}

	public String getUser5Name() {
		return user5Name;
	}

	public void setUser5Name(String user5Name) {
		this.user5Name = user5Name;
	}

	public String getUser5Password() {
		return user5Password;
	}

	public void setUser5Password(String user5Password) {
		this.user5Password = user5Password;
	}

	public int getUser5Priv() {
		return user5Priv;
	}

	public void setUser5Priv(int user5Priv) {
		this.user5Priv = user5Priv;
	}

	public String getUser6Name() {
		return user6Name;
	}

	public void setUser6Name(String user6Name) {
		this.user6Name = user6Name;
	}

	public String getUser6Password() {
		return user6Password;
	}

	public void setUser6Password(String user6Password) {
		this.user6Password = user6Password;
	}

	public int getUser6Priv() {
		return user6Priv;
	}

	public void setUser6Priv(int user6Priv) {
		this.user6Priv = user6Priv;
	}

	public String getUser7Name() {
		return user7Name;
	}

	public void setUser7Name(String user7Name) {
		this.user7Name = user7Name;
	}

	public String getUser7Password() {
		return user7Password;
	}

	public void setUser7Password(String user7Password) {
		this.user7Password = user7Password;
	}

	public int getUser7Priv() {
		return user7Priv;
	}

	public void setUser7Priv(int user7Priv) {
		this.user7Priv = user7Priv;
	}

	public String getUser8Name() {
		return user8Name;
	}

	public void setUser8Name(String user8Name) {
		this.user8Name = user8Name;
	}

	public String getUser8Password() {
		return user8Password;
	}

	public void setUser8Password(String user8Password) {
		this.user8Password = user8Password;
	}

	public int getUser8Priv() {
		return user8Priv;
	}

	public void setUser8Priv(int user8Priv) {
		this.user8Priv = user8Priv;
	}

	public String getFtpServer() {
		return ftpServer;
	}

	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpFolder() {
		return ftpFolder;
	}

	public void setFtpFolder(String ftpFolder) {
		this.ftpFolder = ftpFolder;
	}

	public int getFtpMode() {
		return ftpMode;
	}

	public void setFtpMode(int ftpMode) {
		this.ftpMode = ftpMode;
	}

	public String getFtpUploadInterval() {
		return ftpUploadInterval;
	}

	public void setFtpUploadInterval(String ftpUploadInterval) {
		this.ftpUploadInterval = ftpUploadInterval;
	}

	public String getFtpFilename() {
		return ftpFilename;
	}

	public void setFtpFilename(String ftpFilename) {
		this.ftpFilename = ftpFilename;
	}

	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public String getMailPort() {
		return mailPort;
	}

	public void setMailPort(String mailPort) {
		this.mailPort = mailPort;
	}

	public int getMailMode() {
		return mailMode;
	}

	public void setMailMode(int mailMode) {
		this.mailMode = mailMode;
	}

	public String getMailUser() {
		return mailUser;
	}

	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}

	public String getMailPassword() {
		return mailPassword;
	}

	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}

	public String getMailSender() {
		return mailSender;
	}

	public void setMailSender(String mailSender) {
		this.mailSender = mailSender;
	}

	public String getMailReceiver1() {
		return mailReceiver1;
	}

	public void setMailReceiver1(String mailReceiver1) {
		this.mailReceiver1 = mailReceiver1;
	}

	public String getMailReceiver2() {
		return mailReceiver2;
	}

	public void setMailReceiver2(String mailReceiver2) {
		this.mailReceiver2 = mailReceiver2;
	}

	public String getMailReceiver3() {
		return mailReceiver3;
	}

	public void setMailReceiver3(String mailReceiver3) {
		this.mailReceiver3 = mailReceiver3;
	}

	public String getMailReceiver4() {
		return mailReceiver4;
	}

	public void setMailReceiver4(String mailReceiver4) {
		this.mailReceiver4 = mailReceiver4;
	}

	public boolean isMailReportIP() {
		return mailReportIP;
	}

	public void setMailReportIP(boolean mailReportIP) {
		this.mailReportIP = mailReportIP;
	}

	public int getMotionSensitivity() {
		return motionSensitivity;
	}

	public void setMotionSensitivity(int motionSensitivity) {
		this.motionSensitivity = motionSensitivity;
	}

	public boolean isMotionCompensation() {
		return motionCompensation;
	}

	public void setMotionCompensation(boolean motionCompensation) {
		this.motionCompensation = motionCompensation;
	}

	public int getSoundSensitivity() {
		return soundSensitivity;
	}

	public void setSoundSensitivity(int soundSensitivity) {
		this.soundSensitivity = soundSensitivity;
	}

	public boolean isMailOnAlarm() {
		return mailOnAlarm;
	}

	public void setMailOnAlarm(boolean mailOnAlarm) {
		this.mailOnAlarm = mailOnAlarm;
	}

	public int getUploadOnAlarm() {
		return uploadOnAlarm;
	}

	public void setUploadOnAlarm(int uploadOnAlarm) {
		this.uploadOnAlarm = uploadOnAlarm;
	}

	public boolean isAlarmScheduleEnabled() {
		return alarmScheduleEnabled;
	}

	public void setAlarmScheduleEnabled(boolean alarmScheduleEnabled) {
		this.alarmScheduleEnabled = alarmScheduleEnabled;
	}
	
}