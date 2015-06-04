package com.rhino.foscam.pojo.sd;

import java.util.HashMap;

public class MiscParams {
	
	private boolean disablePreset = false;
	private boolean presetOnBoot = false;
	private boolean centerOnBoot = false;
	private int patrolRoundsVertical = 0;
	private int patrolRoundsHorizontal = 0;
	private int patrolRate = 0;
	private int upwardRate = 0;
	private int downwardRate = 0;
	private int leftwardRate = 0;
	private int rightwaredRate = 0;
	
	public MiscParams() {}
	
	public MiscParams(String response) {
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
		
		String disPre = map.get("ptz_disable_preset");
		this.disablePreset = disPre.equals("1");
		
		String preBoot = map.get("ptz_preset_onstart");
		this.presetOnBoot = preBoot.equals("1");
		
		String centerBoot = map.get("ptz_center_onstart");
		this.centerOnBoot = centerBoot.equals("1");
		
		this.patrolRoundsHorizontal = Integer.parseInt(map.get("ptz_patrol_h_rounds"));
		this.patrolRoundsVertical = Integer.parseInt(map.get("ptz_patrol_v_rounds"));
		this.patrolRate = Integer.parseInt(map.get("ptz_patrol_rate"));
		this.upwardRate = Integer.parseInt(map.get("ptz_patrol_up_rate"));
		this.downwardRate = Integer.parseInt(map.get("ptz_patrol_down_rate"));
		this.leftwardRate = Integer.parseInt(map.get("ptz_patrol_left_rate"));
		this.rightwaredRate = Integer.parseInt(map.get("ptz_patrol_right_rate"));
	}

	public boolean isDisablePreset() {
		return disablePreset;
	}

	public void setDisablePreset(boolean disablePreset) {
		this.disablePreset = disablePreset;
	}

	public boolean isPresetOnBoot() {
		return presetOnBoot;
	}

	public void setPresetOnBoot(boolean presetOnBoot) {
		this.presetOnBoot = presetOnBoot;
	}

	public boolean isCenterOnBoot() {
		return centerOnBoot;
	}

	public void setCenterOnBoot(boolean centerOnBoot) {
		this.centerOnBoot = centerOnBoot;
	}

	public int getPatrolRoundsVertical() {
		return patrolRoundsVertical;
	}

	public void setPatrolRoundsVertical(int patrolRoundsVertical) {
		this.patrolRoundsVertical = patrolRoundsVertical;
	}

	public int getPatrolRoundsHorizontal() {
		return patrolRoundsHorizontal;
	}

	public void setPatrolRoundsHorizontal(int patrolRoundsHorizontal) {
		this.patrolRoundsHorizontal = patrolRoundsHorizontal;
	}

	public int getPatrolRate() {
		return patrolRate;
	}

	public void setPatrolRate(int patrolRate) {
		this.patrolRate = patrolRate;
	}

	public int getUpwardRate() {
		return upwardRate;
	}

	public void setUpwardRate(int upwardRate) {
		this.upwardRate = upwardRate;
	}

	public int getDownwardRate() {
		return downwardRate;
	}

	public void setDownwardRate(int downwardRate) {
		this.downwardRate = downwardRate;
	}

	public int getLeftwardRate() {
		return leftwardRate;
	}

	public void setLeftwardRate(int leftwardRate) {
		this.leftwardRate = leftwardRate;
	}

	public int getRightwaredRate() {
		return rightwaredRate;
	}

	public void setRightwaredRate(int rightwaredRate) {
		this.rightwaredRate = rightwaredRate;
	}

}