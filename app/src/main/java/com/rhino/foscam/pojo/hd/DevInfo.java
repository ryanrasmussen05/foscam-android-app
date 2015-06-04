package com.rhino.foscam.pojo.hd;

public class DevInfo {
	
	private String cameraName;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int timeZone;
	
	public String getCameraName() {
		return cameraName;
	}
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public int getDay() {
		return day;
	}
	public int getHour() {
		return hour;
	}
	public int getMinute() {
		return minute;
	}
	public int getSecond() {
		return second;
	}
	public int getTimeZone() {
		return timeZone;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public void setSecond(int second) {
		this.second = second;
	}
	public void setTimeZone(int timeZone) {
		this.timeZone = timeZone;
	}
}
