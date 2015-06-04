package com.rhino.foscam.accessor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.rhino.foscam.exception.BadRequestException;
import com.rhino.foscam.exception.GeneralException;
import com.rhino.foscam.exception.UnauthorizedException;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.sd.AlarmSchedule;
import com.rhino.foscam.pojo.sd.CameraParams;
import com.rhino.foscam.pojo.sd.CameraStatus;
import com.rhino.foscam.pojo.sd.FTPTestResult;
import com.rhino.foscam.pojo.sd.MailTestResult;
import com.rhino.foscam.pojo.sd.MiscParams;
import com.rhino.foscam.pojo.sd.VideoParams;

@SuppressWarnings("deprecation")
public class CameraUtilsSD {
	
	public static final String GET_PARAMS = "/get_params.cgi?";
	public static final String GET_LOG = "/get_log.cgi?";
	public static final String GET_STATUS = "/get_status.cgi?";
	public static final String GET_MISC = "/get_misc.cgi?";
	public static final String GET_VIDEO_PARAMS = "/get_camera_params.cgi?";
	public static final String SET_ALARM = "/set_alarm.cgi?";
	public static final String SET_ALIAS = "/set_alias.cgi?";
	public static final String SET_DATE_TIME = "/set_datetime.cgi?";
	public static final String SET_USERS = "/set_users.cgi?";
	public static final String SET_FTP = "/set_ftp.cgi?";
	public static final String SET_MAIL = "/set_mail.cgi?";
	public static final String SET_MISC = "/set_misc.cgi?";
	public static final String TEST_FTP = "/test_ftp.cgi?";
	public static final String TEST_MAIL = "/test_mail.cgi?";
	public static final String REBOOT = "/reboot.cgi?";
	public static final String SNAPSHOT = "/snapshot.cgi?";
	public static final String VIDEOSTREAM = "/videostream.cgi?";
	public static final String DECODER = "/decoder_control.cgi?";
	public static final String VIDEO_SETTINGS = "/camera_control.cgi?";
	
	public static final int COMMAND_UP = 0;
	public static final int COMMAND_UP_STOP = 1;
	public static final int COMMAND_DOWN = 2;
	public static final int COMMAND_DOWN_STOP = 3;
	public static final int COMMAND_LEFT = 6;
	public static final int COMMAND_LEFT_STOP = 7;
	public static final int COMMAND_RIGHT = 4;
	public static final int COMMAND_RIGHT_STOP = 5;
	public static final int COMMAND_PRESET_SET_1 = 30;
	public static final int COMMAND_PRESET_GOTO_1 = 31;
	public static final int COMMAND_PRESET_SET_2 = 32;
	public static final int COMMAND_PRESET_GOTO_2 = 33;
	public static final int COMMAND_PRESET_SET_3 = 34;
	public static final int COMMAND_PRESET_GOTO_3 = 35;
	public static final int COMMAND_PRESET_SET_4 = 36;
	public static final int COMMAND_PRESET_GOTO_4 = 37;
	public static final int COMMAND_PRESET_SET_5 = 38;
	public static final int COMMAND_PRESET_GOTO_5 = 39;
	public static final int COMMAND_PRESET_SET_6 = 40;
	public static final int COMMAND_PRESET_GOTO_6 = 41;
	public static final int COMMAND_PRESET_SET_7 = 42;
	public static final int COMMAND_PRESET_GOTO_7 = 43;
	public static final int COMMAND_PRESET_SET_8 = 44;
	public static final int COMMAND_PRESET_GOTO_8 = 45;
	public static final int COMMAND_IR_OFF = 94;
	public static final int COMMAND_IR_ON = 95;
	
	public static CameraParams getCameraParams(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, GET_PARAMS);
		String response = execute(url);
		
		return new CameraParams(response);
	}
	
	public static String getCameraLog(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, GET_LOG);
		String response = execute(url);
		return response;
	}
	
	public static CameraStatus getCameraStatus(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, GET_STATUS);
		String response = execute(url);
		
		return new CameraStatus(response);
	}
	
	public static MiscParams getMiscParams(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, GET_MISC);
		String response = execute(url);
		
		return new MiscParams(response);
	}
	
	public static VideoParams getVideoParams(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, GET_VIDEO_PARAMS);
		String response = execute(url);
		
		return new VideoParams(response);
	}
	
	public static AlarmSchedule getAlarmSchedule(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, GET_PARAMS);
		String response = execute(url);
		
		return new AlarmSchedule(response);
	}
	
	public static boolean setAlarmSettings(Camera camera, CameraParams cameraParams) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, SET_ALARM);
		url += "&motion_sensitivity=" + cameraParams.getMotionSensitivity();
		url += "&motion_compensation=";
		if(cameraParams.isMotionCompensation()){url += "1";}else{url += "0";}
		url += "&sound_sensitivity=" + cameraParams.getSoundSensitivity();
		url += "&mail=";
		if(cameraParams.isMailOnAlarm()){url += "1";}else{url += "0";}
		url += "&upload_interval=" + cameraParams.getUploadOnAlarm();
		url += "&schedule_enable=";
		if(cameraParams.isAlarmScheduleEnabled()){url += "1";}else{url += "0";}
		url += "&motion_armed=";
		if(cameraParams.isMotionAlarmEnabled()){url += "1";}else{url += "0";}
		url += "&sounddetect_armed=";
		if(cameraParams.isSoundAlarmEnabled()){url += "1";}else{url += "0";}
		
		return executeSet(url);
	}
	
	public static boolean setAlarmSchedule(Camera camera, AlarmSchedule sched) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, SET_ALARM);
		url += "&schedule_sun_0=" + sched.getSunday0Coded();
		url += "&schedule_sun_1=" + sched.getSunday1Coded();
		url += "&schedule_sun_2=" + sched.getSunday2Coded();
		url += "&schedule_mon_0=" + sched.getMonday0Coded();
		url += "&schedule_mon_1=" + sched.getMonday1Coded();
		url += "&schedule_mon_2=" + sched.getMonday2Coded();
		url += "&schedule_tue_0=" + sched.getTuesday0Coded();
		url += "&schedule_tue_1=" + sched.getTuesday1Coded();
		url += "&schedule_tue_2=" + sched.getTuesday2Coded();
		url += "&schedule_wed_0=" + sched.getWednesday0Coded();
		url += "&schedule_wed_1=" + sched.getWednesday1Coded();
		url += "&schedule_wed_2=" + sched.getWednesday2Coded();
		url += "&schedule_thu_0=" + sched.getThursday0Coded();
		url += "&schedule_thu_1=" + sched.getThursday1Coded();
		url += "&schedule_thu_2=" + sched.getThursday2Coded();
		url += "&schedule_fri_0=" + sched.getFriday0Coded();
		url += "&schedule_fri_1=" + sched.getFriday1Coded();
		url += "&schedule_fri_2=" + sched.getFriday2Coded();
		url += "&schedule_sat_0=" + sched.getSaturday0Coded();
		url += "&schedule_sat_1=" + sched.getSaturday1Coded();
		url += "&schedule_sat_2=" + sched.getSaturday2Coded();
		
		return executeSet(url);
	}
	
	public static boolean setAlias(Camera camera, String newAlias) throws BadRequestException, UnauthorizedException, GeneralException, UnsupportedEncodingException {
		newAlias = URLEncoder.encode(newAlias, "UTF-8");
		String url = getBaseUrl(camera, SET_ALIAS) + "&alias=" + newAlias;
		return executeSet(url);
	}
	
	public static boolean setDateTime(Camera camera, CameraParams cameraParams) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, SET_DATE_TIME);
		url += "&tz=" + cameraParams.getTimeZone();
		url += "&daylight_saving_time=" + cameraParams.getDst();
		String ntp = "0";
		if(cameraParams.isNtpEnabled()){ntp = "1";}
		url += "&ntp_enable=" + ntp;
		url += "&ntp_svr=" + cameraParams.getNtpServer();
		return executeSet(url);
	}
	
	public static boolean setusers(Camera camera, CameraParams cameraParams) throws UnsupportedEncodingException, BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, SET_USERS);
		url += "&user1=" + URLEncoder.encode(cameraParams.getUser1Name(), "UTF-8");
		url += "&pwd1=" + URLEncoder.encode(cameraParams.getUser1Password(), "UTF-8");
		url += "&pri1=" + URLEncoder.encode(String.valueOf(cameraParams.getUser1Priv()), "UTF-8");
		url += "&user2=" + URLEncoder.encode(cameraParams.getUser2Name(), "UTF-8");
		url += "&pwd2=" + URLEncoder.encode(cameraParams.getUser2Password(), "UTF-8");
		url += "&pri2=" + URLEncoder.encode(String.valueOf(cameraParams.getUser2Priv()), "UTF-8");
		url += "&user3=" + URLEncoder.encode(cameraParams.getUser3Name(), "UTF-8");
		url += "&pwd3=" + URLEncoder.encode(cameraParams.getUser3Password(), "UTF-8");
		url += "&pri3=" + URLEncoder.encode(String.valueOf(cameraParams.getUser3Priv()), "UTF-8");
		url += "&user4=" + URLEncoder.encode(cameraParams.getUser4Name(), "UTF-8");
		url += "&pwd4=" + URLEncoder.encode(cameraParams.getUser4Password(), "UTF-8");
		url += "&pri4=" + URLEncoder.encode(String.valueOf(cameraParams.getUser4Priv()), "UTF-8");
		url += "&user5=" + URLEncoder.encode(cameraParams.getUser5Name(), "UTF-8");
		url += "&pwd5=" + URLEncoder.encode(cameraParams.getUser5Password(), "UTF-8");
		url += "&pri5=" + URLEncoder.encode(String.valueOf(cameraParams.getUser5Priv()), "UTF-8");
		url += "&user6=" + URLEncoder.encode(cameraParams.getUser6Name(), "UTF-8");
		url += "&pwd6=" + URLEncoder.encode(cameraParams.getUser6Password(), "UTF-8");
		url += "&pri6=" + URLEncoder.encode(String.valueOf(cameraParams.getUser6Priv()), "UTF-8");
		url += "&user7=" + URLEncoder.encode(cameraParams.getUser7Name(), "UTF-8");
		url += "&pwd7=" + URLEncoder.encode(cameraParams.getUser7Password(), "UTF-8");
		url += "&pri7=" + URLEncoder.encode(String.valueOf(cameraParams.getUser7Priv()), "UTF-8");
		url += "&user8=" + URLEncoder.encode(cameraParams.getUser8Name(), "UTF-8");
		url += "&pwd8=" + URLEncoder.encode(cameraParams.getUser8Password(), "UTF-8");
		url += "&pri8=" + URLEncoder.encode(String.valueOf(cameraParams.getUser8Priv()), "UTF-8");
		return executeSet(url);
	}
	
	public static boolean setFtp(Camera camera, CameraParams cameraParams) throws UnsupportedEncodingException, BadRequestException, UnauthorizedException, GeneralException {
		String url = "http://" + camera.getCameraUrl() + ":" + camera.getPort() + SET_FTP + "cam_user=" + camera.getUsername() + "&cam_pwd=" + camera.getPassword();
		url += "&svr=" + URLEncoder.encode(cameraParams.getFtpServer(), "UTF-8");
		url += "&port=" + URLEncoder.encode(cameraParams.getFtpPort(), "UTF-8");
		url += "&user=" + URLEncoder.encode(cameraParams.getFtpUser(), "UTF-8");
		url += "&pwd=" + URLEncoder.encode(cameraParams.getFtpPassword(), "UTF-8");
		url += "&dir=" + URLEncoder.encode(cameraParams.getFtpFolder(), "UTF-8");
		url += "&mode=" + URLEncoder.encode(String.valueOf(cameraParams.getFtpMode()), "UTF-8");
		url += "&upload_interval=" + URLEncoder.encode(cameraParams.getFtpUploadInterval(), "UTF-8");
		url += "&filename=" + URLEncoder.encode(cameraParams.getFtpFilename(), "UTF-8");
		url += "&numberoffiles=";
		if(cameraParams.getFtpFilename().isEmpty()) {url += "0";} else {url += "999";}
		return executeSet(url);
	}
	
	public static boolean setMail(Camera camera, CameraParams cameraParams) throws UnsupportedEncodingException, BadRequestException, UnauthorizedException, GeneralException {
		String url = "http://" + camera.getCameraUrl() + ":" + camera.getPort() + SET_MAIL + "cam_user=" + camera.getUsername() + "&cam_pwd=" + camera.getPassword();
		url += "&svr=" + URLEncoder.encode(cameraParams.getMailServer(), "UTF-8");
		url += "&port=" + URLEncoder.encode(cameraParams.getMailPort(), "UTF-8");
		url += "&tls=" + URLEncoder.encode(String.valueOf(cameraParams.getMailMode()), "UTF-8");
		url += "&user=" + URLEncoder.encode(cameraParams.getMailUser(), "UTF-8");
		url += "&pwd=" + URLEncoder.encode(cameraParams.getMailPassword(), "UTF-8");
		url += "&sender=" + URLEncoder.encode(cameraParams.getMailSender(), "UTF-8");
		url += "&receiver1=" + URLEncoder.encode(cameraParams.getMailReceiver1(), "UTF-8");
		url += "&receiver2=" + URLEncoder.encode(cameraParams.getMailReceiver2(), "UTF-8");
		url += "&receiver3=" + URLEncoder.encode(cameraParams.getMailReceiver3(), "UTF-8");
		url += "&receiver4=" + URLEncoder.encode(cameraParams.getMailReceiver4(), "UTF-8");
		url += "&mail_inet_ip=";
		if(cameraParams.isMailReportIP()) {url += "1";} else {url += "0";}
		return executeSet(url);
	}
	
	public static boolean setMisc(Camera camera, MiscParams misc) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, SET_MISC);
		url += "&ptz_disable_preset=";
		if(misc.isDisablePreset()){url += "1";}else{url += "0";}
		url += "&ptz_preset_onstart=";
		if(misc.isPresetOnBoot()){url += "1";}else{url += "0";}
		url += "&ptz_center_onstart=";
		if(misc.isCenterOnBoot()){url += "1";}else{url += "0";}
		url += "&ptz_patrol_h_rounds=" + misc.getPatrolRoundsHorizontal();
		url += "&ptz_patrol_v_rounds=" + misc.getPatrolRoundsVertical();
		url += "&ptz_patrol_rate=" + misc.getPatrolRate();
		url += "&ptz_patrol_up_rate=" + misc.getUpwardRate();	
		url += "&ptz_patrol_down_rate=" + misc.getDownwardRate();
		url += "&ptz_patrol_right_rate=" + misc.getRightwaredRate();
		url += "&ptz_patrol_left_rate=" + misc.getLeftwardRate();
		return executeSet(url);
	}
	
	public static boolean setVideoResolution(Camera camera, VideoParams params) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, VIDEO_SETTINGS);
		url += "&param=0&value=" + params.getURLResolution();
		return executeSet(url);
	}
	
	public static boolean setVideoBrightness(Camera camera, VideoParams params) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, VIDEO_SETTINGS);
		url += "&param=1&value=" + params.getURLBrightness();
		return executeSet(url);
	}
	
	public static boolean setVideoContrast(Camera camera, VideoParams params) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, VIDEO_SETTINGS);
		url += "&param=2&value=" + params.getContrast();
		return executeSet(url);
	}
	
	public static boolean setVideoMode(Camera camera, VideoParams params) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, VIDEO_SETTINGS);
		url += "&param=3&value=" + params.getMode();
		return executeSet(url);
	}	
	
	public static boolean setVideoFlip(Camera camera, VideoParams params) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, VIDEO_SETTINGS);
		url += "&param=5&value=" + params.getFlip();
		return executeSet(url);
	}	
	
	public static FTPTestResult testFTP(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, TEST_FTP);
		String response = execute(url);
		
		return new FTPTestResult(response);
	}
	
	public static MailTestResult testMail(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException {
		String url = getBaseUrl(camera, TEST_MAIL);
		String response = execute(url);
		
		return new MailTestResult(response);
	}
	
	public static boolean rebootCamera(Camera camera) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, REBOOT);
		return executeSet(url);
	}
	
	public static boolean cameraControl(Camera camera, int command) throws BadRequestException, UnauthorizedException {
		String url = getBaseUrl(camera, DECODER);
		url += "&command=" + command;
		return executeSet(url);
	}
	
	@SuppressWarnings("resource")
	public static Bitmap snapshot(Camera camera) throws BadRequestException, GeneralException {
		String url = getBaseUrl(camera, SNAPSHOT);
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		
		try {
			response = client.execute(get);
		} catch (IOException e) {
			throw new BadRequestException();
		}
		
		if(response.getStatusLine().getStatusCode() != 200) {
			throw new BadRequestException();
		}
		
		Bitmap snapshot;
		
		try {
			InputStream is = response.getEntity().getContent();
			BufferedInputStream bis = new BufferedInputStream(is);
			snapshot = BitmapFactory.decodeStream(is);
			bis.close();
			is.close();
		} catch (IOException e) {
			throw new GeneralException();
		}
		
		return snapshot;
	}
	
	@SuppressWarnings("resource")
	public static MjpegInputStream videoStream(Camera camera) throws GeneralException, BadRequestException {
		String url = getBaseUrl(camera, VIDEOSTREAM);
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		
		try {
			response = client.execute(get);
		} catch (IOException e) {
			throw new BadRequestException();
		}
		
		MjpegInputStream stream;
		
		try {
			stream = new MjpegInputStream(response.getEntity().getContent());
		} catch (Exception e) {
			throw new GeneralException();
		}
		
		return stream;
	}
	
	@SuppressWarnings("resource")
	private static String execute(String url) throws BadRequestException, UnauthorizedException, GeneralException {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		
		try {
			response = client.execute(get);
		} catch (IOException e) {
			throw new BadRequestException();
		}
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		switch (responseCode) {
		case 401:
			throw new UnauthorizedException();
		case 404:
			throw new BadRequestException();
		default:
			break;
		}
		
		StringBuffer result = new StringBuffer();
		
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch(IOException e) {
			throw new GeneralException();
		}
		
		return result.toString();
	}
	
	@SuppressWarnings("resource")
	private static boolean executeSet(String url) throws BadRequestException, UnauthorizedException {
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		
		try {
			response = client.execute(get);
		} catch (IOException e) {
			throw new BadRequestException();
		}
		
		int responseCode = response.getStatusLine().getStatusCode();
		
		switch (responseCode) {
		case 401:
			throw new UnauthorizedException();
		case 404:
			throw new BadRequestException();
		default:
			break;
		}
				
		if(responseCode == 200) {
			return true;
		} else {
			return false;
		}
	}
	
	private static String getBaseUrl(Camera camera, String action) {
		String base = "http://";
		if(camera.getCameraUrl().contains("http://") || camera.getCameraUrl().contains("https://")) {
			base = "";
		}
		String url = base + camera.getCameraUrl() + ":" + camera.getPort() +
				action + "user=" + camera.getUsername() + "&pwd=" + camera.getPassword();
		return url;
	}

}