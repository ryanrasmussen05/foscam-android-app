package com.rhino.foscam.pojo.sd;

import java.util.HashMap;

public class VideoParams {
	
	private int resolution;
	private int brightness;
	private int contrast;
	private int mode;
	private int flip;
	
	public VideoParams(){
		this.resolution = -1;
		this.brightness = -1;
		this.contrast = -1;
		this.mode = -1;
		this.flip = -1;
	}
	
	public VideoParams(String response) {
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
		
		this.resolution = Integer.parseInt(map.get("resolution"));
		this.brightness = Integer.parseInt(map.get("brightness"));
		this.contrast = Integer.parseInt(map.get("contrast"));
		this.mode = Integer.parseInt(map.get("mode"));
		this.flip = Integer.parseInt(map.get("flip"));
	}

	public int getResolution() {
		if(resolution == 32) {
			return 0;
		} else {
			return 1;
		}
	}
	
	public int getURLResolution() {
		return resolution;
	}

	public void setResolution(int resolution) {
		if(resolution == 0) {
			this.resolution = 32;
		} else {
			this.resolution = 8;
		}
	}

	public int getBrightness() {
		return brightness / 16;
	}
	
	public int getURLBrightness() {
		return brightness;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness * 16;
	}

	public int getContrast() {
		return contrast;
	}

	public void setContrast(int contrast) {
		this.contrast = contrast;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getFlip() {
		return flip;
	}

	public boolean isFlipped() {
		return (flip == 1 || flip == 3);
	}
	
	public boolean isMirrored() {
		return (flip == 2 || flip == 3);
	}
	
	public void setFlip(boolean flip, boolean mirror) {
		if(flip && mirror) {
			this.flip = 3;
		} else if(!flip && mirror) {
			this.flip = 2;
		} else if(flip && !mirror) {
			this.flip = 1;
		} else {
			this.flip = 0;
		}
	}
	
}