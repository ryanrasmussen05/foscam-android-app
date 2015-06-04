package com.rhino.foscam.accessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.rhino.foscam.exception.BadRequestException;
import com.rhino.foscam.exception.GeneralException;
import com.rhino.foscam.exception.UnauthorizedException;
import com.rhino.foscam.pojo.Camera;
import com.rhino.foscam.pojo.hd.DevInfo;
import com.rhino.foscam.pojo.hd.FTP;
import com.rhino.foscam.pojo.hd.HDLog;
import com.rhino.foscam.pojo.hd.SystemTime;

@SuppressWarnings("deprecation")
public class CameraUtilsHD {
	
	public static final String GET_LOG = "getLog";
	public static final String GET_INFO = "getDevInfo";
	public static final String SET_NAME = "setDevName";
    public static final String GET_TIME = "getSystemTime";
    public static final String SET_TIME = "setSystemTime";
    public static final String GET_FTP = "getFtpConfig";
    public static final String SET_FTP = "setFtpConfig";
    public static final String TEST_FTP = "testFtpServer";

	public static String getCameraLog(Camera camera) throws BadRequestException, UnauthorizedException, GeneralException, Exception {
		int offset = 0;
		HDLog log = new HDLog();
		
		while(!log.isComplete()) {
			String url = getBaseUrl(camera, GET_LOG);
			url += "&offset=" + String.valueOf(offset);
			url += "&count=10";
			String response = URLDecoder.decode(execute(url));
			
			log = XMLUtils.parseLog(response, log, offset);
			offset += 10;
		}
		
		return log.getText();
	}
	
	public static DevInfo getDevInfo(Camera camera) throws Exception {
		String url = getBaseUrl(camera, GET_INFO);
		String response = URLDecoder.decode(execute(url));
		
		return XMLUtils.parseDevInfo(response);
	}

    public static SystemTime getSystemTime(Camera camera) throws Exception {
        String url = getBaseUrl(camera, GET_TIME);
        String response = URLDecoder.decode(execute(url));

        return XMLUtils.parseSystemTime(response);
    }

    public static FTP getFTPConfig(Camera camera) throws Exception {
        String url = getBaseUrl(camera, GET_FTP);
        String response = URLDecoder.decode(execute(url));

        return XMLUtils.parseFTP(response);
    }

	public static boolean setDevName(Camera camera, String name) throws BadRequestException, UnauthorizedException, UnsupportedEncodingException {
		String url = getBaseUrl(camera, SET_NAME);
		url += "&devName=" + URLEncoder.encode(name, "UTF-8");
		
		return executeSet(url);
	}

    public static boolean setSystemTime(Camera camera, SystemTime systemTime) throws BadRequestException, UnauthorizedException, UnsupportedEncodingException {
        String url = getBaseUrl(camera, SET_TIME);
        url += "&timeSource=" + URLEncoder.encode(systemTime.getTimeSource(), "UTF-8");
        url += "&ntpServer=" + URLEncoder.encode(systemTime.getNtpServer(), "UTF-8");
        url += "&dateFormat=" + URLEncoder.encode(systemTime.getStringDateFormat(), "UTF-8");
        url += "&timeFormat=" + URLEncoder.encode(systemTime.getStringTimeFormat(), "UTF-8");
        url += "&timeZone=" + URLEncoder.encode(systemTime.getStringTimeZone(), "UTF-8");
        url += "&isDst=" + URLEncoder.encode(systemTime.getIsDst(), "UTF-8");

        return executeSet(url);
    }

    public static boolean setFTPConfig(Camera camera, FTP ftp) throws BadRequestException, UnauthorizedException, UnsupportedEncodingException {
        String url = getBaseUrl(camera, SET_FTP);
        url += "&ftpAddr=" + URLEncoder.encode(ftp.getFtpAddr(), "UTF-8");
        url += "&ftpPort=" + URLEncoder.encode(ftp.getFtpPort(), "UTF-8");
        url += "&mode=" + URLEncoder.encode(ftp.getMode(), "UTF-8");
        url += "&userName=" + URLEncoder.encode(ftp.getUserName(), "UTF-8");
        url += "&password=" + URLEncoder.encode(ftp.getPassword(), "UTF-8");

        return executeSet(url);
    }

    public static String testFTP(Camera camera, FTP ftp) throws Exception {
        String url = getBaseUrl(camera, TEST_FTP);
        url += "&ftpAddr=" + URLEncoder.encode(ftp.getFtpAddr(), "UTF-8");
        url += "&ftpPort=" + URLEncoder.encode(ftp.getFtpPort(), "UTF-8");
        url += "&mode=" + URLEncoder.encode(ftp.getMode(), "UTF-8");
        url += "&userName=" + URLEncoder.encode(ftp.getUserName(), "UTF-8");
        url += "&password=" + URLEncoder.encode(ftp.getPassword(), "UTF-8");

        String response = URLDecoder.decode(execute(url));
        return XMLUtils.parseFTPTest(response);
    }
	
	private static String getBaseUrl(Camera camera, String action) {
		String base = "http://";
		if(camera.getCameraUrl().contains("http://") || camera.getCameraUrl().contains("https://")) {
			base = "";
		}
		return base + camera.getCameraUrl() + ":" + camera.getPort() + "/cgi-bin/CGIProxy.fcgi?cmd=" +
				action + "&usr=" + camera.getUsername() + "&pwd=" + camera.getPassword();
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
}
