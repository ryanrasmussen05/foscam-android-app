package com.rhino.foscam.pojo.sd;

import java.math.BigInteger;
import java.util.HashMap;

public class AlarmSchedule {

	private long sunday0;
	private long sunday1;
	private long sunday2;
	private long monday0;
	private long monday1;
	private long monday2;
	private long tuesday0;
	private long tuesday1;
	private long tuesday2;
	private long wednesday0;
	private long wednesday1;
	private long wednesday2;
	private long thursday0;
	private long thursday1;
	private long thursday2;
	private long friday0;
	private long friday1;
	private long friday2;
	private long saturday0;
	private long saturday1;
	private long saturday2;
	
	private boolean[] sunday = new boolean[96];
	private boolean[] monday = new boolean[96]; 
	private boolean[] tuesday = new boolean[96]; 
	private boolean[] wednesday = new boolean[96]; 
	private boolean[] thursday = new boolean[96]; 
	private boolean[] friday = new boolean[96]; 
	private boolean[] saturday = new boolean[96]; 
	
	public AlarmSchedule() {}
	
	public AlarmSchedule(String response) {
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

		this.sunday0 = Long.parseLong(map.get("alarm_schedule_sun_0"));
		this.sunday1 = Long.parseLong(map.get("alarm_schedule_sun_1"));
		this.sunday2 = Long.parseLong(map.get("alarm_schedule_sun_2"));
		this.monday0 = Long.parseLong(map.get("alarm_schedule_mon_0"));
		this.monday1 = Long.parseLong(map.get("alarm_schedule_mon_1"));
		this.monday2 = Long.parseLong(map.get("alarm_schedule_mon_2"));
		this.tuesday0 = Long.parseLong(map.get("alarm_schedule_tue_0"));
		this.tuesday1 = Long.parseLong(map.get("alarm_schedule_tue_1"));
		this.tuesday2 = Long.parseLong(map.get("alarm_schedule_tue_2"));
		this.wednesday0 = Long.parseLong(map.get("alarm_schedule_wed_0"));
		this.wednesday1 = Long.parseLong(map.get("alarm_schedule_wed_1"));
		this.wednesday2 = Long.parseLong(map.get("alarm_schedule_wed_2"));
		this.thursday0 = Long.parseLong(map.get("alarm_schedule_thu_0"));
		this.thursday1 = Long.parseLong(map.get("alarm_schedule_thu_1"));
		this.thursday2 = Long.parseLong(map.get("alarm_schedule_thu_2"));
		this.friday0 = Long.parseLong(map.get("alarm_schedule_fri_0"));
		this.friday1 = Long.parseLong(map.get("alarm_schedule_fri_1"));
		this.friday2 = Long.parseLong(map.get("alarm_schedule_fri_2"));
		this.saturday0 = Long.parseLong(map.get("alarm_schedule_sat_0"));
		this.saturday1 = Long.parseLong(map.get("alarm_schedule_sat_1"));
		this.saturday2 = Long.parseLong(map.get("alarm_schedule_sat_2"));
		
		String sundayBits0 = fixBits(Long.toBinaryString(sunday0));
		String sundayBits1 = fixBits(Long.toBinaryString(sunday1));
		String sundayBits2 = fixBits(Long.toBinaryString(sunday2));
		String mondayBits0 = fixBits(Long.toBinaryString(monday0));
		String mondayBits1 = fixBits(Long.toBinaryString(monday1));
		String mondayBits2 = fixBits(Long.toBinaryString(monday2));
		String tuesdayBits0 = fixBits(Long.toBinaryString(tuesday0));
		String tuesdayBits1 = fixBits(Long.toBinaryString(tuesday1));
		String tuesdayBits2 = fixBits(Long.toBinaryString(tuesday2));
		String wednesdayBits0 = fixBits(Long.toBinaryString(wednesday0));
		String wednesdayBits1 = fixBits(Long.toBinaryString(wednesday1));
		String wednesdayBits2 = fixBits(Long.toBinaryString(wednesday2));
		String thursdayBits0 = fixBits(Long.toBinaryString(thursday0));
		String thursdayBits1 = fixBits(Long.toBinaryString(thursday1));
		String thursdayBits2 = fixBits(Long.toBinaryString(thursday2));
		String fridayBits0 = fixBits(Long.toBinaryString(friday0));
		String fridayBits1 = fixBits(Long.toBinaryString(friday1));
		String fridayBits2 = fixBits(Long.toBinaryString(friday2));
		String saturdayBits0 = fixBits(Long.toBinaryString(saturday0));
		String saturdayBits1 = fixBits(Long.toBinaryString(saturday1));
		String saturdayBits2 = fixBits(Long.toBinaryString(saturday2));
		
		for(int index = 0; index < 32; index++) {
			if(sundayBits0.charAt(index) == '1') {
				sunday[31 - index] = true;
			} else {
				sunday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(sundayBits1.charAt(index) == '1') {
				sunday[63 - index] = true;
			} else {
				sunday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(sundayBits2.charAt(index) == '1') {
				sunday[95 - index] = true;
			} else {
				sunday[95 - index] = false;
			}
		}
		
		for(int index = 0; index < 32; index++) {
			if(mondayBits0.charAt(index) == '1') {
				monday[31 - index] = true;
			} else {
				monday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(mondayBits1.charAt(index) == '1') {
				monday[63 - index] = true;
			} else {
				monday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(mondayBits2.charAt(index) == '1') {
				monday[95 - index] = true;
			} else {
				monday[95 - index] = false;
			}
		}
		
		for(int index = 0; index < 32; index++) {
			if(tuesdayBits0.charAt(index) == '1') {
				tuesday[31 - index] = true;
			} else {
				tuesday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(tuesdayBits1.charAt(index) == '1') {
				tuesday[63 - index] = true;
			} else {
				tuesday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(tuesdayBits2.charAt(index) == '1') {
				tuesday[95 - index] = true;
			} else {
				tuesday[95 - index] = false;
			}
		}
		
		for(int index = 0; index < 32; index++) {
			if(wednesdayBits0.charAt(index) == '1') {
				wednesday[31 - index] = true;
			} else {
				wednesday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(wednesdayBits1.charAt(index) == '1') {
				wednesday[63 - index] = true;
			} else {
				wednesday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(wednesdayBits2.charAt(index) == '1') {
				wednesday[95 - index] = true;
			} else {
				wednesday[95 - index] = false;
			}
		}
		
		for(int index = 0; index < 32; index++) {
			if(thursdayBits0.charAt(index) == '1') {
				thursday[31 - index] = true;
			} else {
				thursday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(thursdayBits1.charAt(index) == '1') {
				thursday[63 - index] = true;
			} else {
				thursday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(thursdayBits2.charAt(index) == '1') {
				thursday[95 - index] = true;
			} else {
				thursday[95 - index] = false;
			}
		}
		
		for(int index = 0; index < 32; index++) {
			if(fridayBits0.charAt(index) == '1') {
				friday[31 - index] = true;
			} else {
				friday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(fridayBits1.charAt(index) == '1') {
				friday[63 - index] = true;
			} else {
				friday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(fridayBits2.charAt(index) == '1') {
				friday[95 - index] = true;
			} else {
				friday[95 - index] = false;
			}
		}
		
		for(int index = 0; index < 32; index++) {
			if(saturdayBits0.charAt(index) == '1') {
				saturday[31 - index] = true;
			} else {
				saturday[31 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(saturdayBits1.charAt(index) == '1') {
				saturday[63 - index] = true;
			} else {
				saturday[63 - index] = false;
			}
		}
		for(int index = 0; index < 32; index++) {
			if(saturdayBits2.charAt(index) == '1') {
				saturday[95 - index] = true;
			} else {
				saturday[95 - index] = false;
			}
		}
	}

	public boolean[] getSunday() {
		return sunday;
	}

	public boolean[] getMonday() {
		return monday;
	}

	public boolean[] getTuesday() {
		return tuesday;
	}

	public boolean[] getWednesday() {
		return wednesday;
	}

	public boolean[] getThursday() {
		return thursday;
	}

	public boolean[] getFriday() {
		return friday;
	}

	public boolean[] getSaturday() {
		return saturday;
	}
	
	public String fixBits(String bits) {
		if(bits.length() < 32) {
			int difference = 32 - bits.length();
			for(int index = 0; index < difference; index++) {
				bits = "0" + bits;
			}
		}
		return bits;
	}
	
	public long getSunday0Coded() {
		return getCodedDay(sunday)[0];
	}
	
	public long getSunday1Coded() {
		return getCodedDay(sunday)[1];
	}
	
	public long getSunday2Coded() {
		return getCodedDay(sunday)[2];
	}
	
	public long getMonday0Coded() {
		return getCodedDay(monday)[0];
	}
	
	public long getMonday1Coded() {
		return getCodedDay(monday)[1];
	}
	
	public long getMonday2Coded() {
		return getCodedDay(monday)[2];
	}
	
	public long getTuesday0Coded() {
		return getCodedDay(tuesday)[0];
	}
	
	public long getTuesday1Coded() {
		return getCodedDay(tuesday)[1];
	}
	
	public long getTuesday2Coded() {
		return getCodedDay(tuesday)[2];
	}
	
	public long getWednesday0Coded() {
		return getCodedDay(wednesday)[0];
	}
	
	public long getWednesday1Coded() {
		return getCodedDay(wednesday)[1];
	}
	
	public long getWednesday2Coded() {
		return getCodedDay(wednesday)[2];
	}
	
	public long getThursday0Coded() {
		return getCodedDay(thursday)[0];
	}
	
	public long getThursday1Coded() {
		return getCodedDay(thursday)[1];
	}
	
	public long getThursday2Coded() {
		return getCodedDay(thursday)[2];
	}
	
	public long getFriday0Coded() {
		return getCodedDay(friday)[0];
	}
	
	public long getFriday1Coded() {
		return getCodedDay(friday)[1];
	}
	
	public long getFriday2Coded() {
		return getCodedDay(friday)[2];
	}
	
	public long getSaturday0Coded() {
		return getCodedDay(saturday)[0];
	}
	
	public long getSaturday1Coded() {
		return getCodedDay(saturday)[1];
	}
	
	public long getSaturday2Coded() {
		return getCodedDay(saturday)[2];
	}
	
	private long[] getCodedDay(boolean[] day) {
		String day0 = "";
		String day1 = "";
		String day2 = "";
		
		for(int index = 0; index < 32; index++) {
			if(day[index]) {
				day0 = "1" + day0;
			} else {
				day0 = "0" + day0;
			}
		}
		for(int index = 32; index < 64; index++) {
			if(day[index]) {
				day1 = "1" + day1;
			} else {
				day1 = "0" + day1;
			}
		}
		for(int index = 64; index < 96; index++) {
			if(day[index]) {
				day2 = "1" + day2;
			} else {
				day2 = "0" + day2;
			}
		}
		
		long[] ret = new long[3];
		ret[0] = new BigInteger(day0, 2).intValue();
		ret[1] = new BigInteger(day1, 2).intValue();
		ret[2] = new BigInteger(day2, 2).intValue();
		return ret;
	}
	
}
