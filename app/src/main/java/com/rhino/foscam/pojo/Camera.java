package com.rhino.foscam.pojo;

public class Camera {
	
	private String cameraName;
	private String cameraUrl;
	private String port;
	private String username;
	private String password;
	private String type;
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	public String getCameraUrl() {
		return cameraUrl;
	}
	public void setCameraUrl(String cameraUrl) {
		this.cameraUrl = cameraUrl;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isHD() {
		if(type.equals("0")) {
			return false;
		}
		return true;
	}
}
