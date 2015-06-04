package com.rhino.foscam.pojo.hd;

public class SystemTime {
    private String timeSource;
    private String ntpServer;
    private String dateFormat;
    private String timeFormat;
    private String timeZone;
    private String isDst;
    private String year;
    private String month;
    private String day;
    private String hour;
    private String min;

    public String getNtpServer() {
        return ntpServer;
    }

    public void setNtpServer(String ntpServer) {
        this.ntpServer = ntpServer;
    }

    public Integer getDateFormat() {
        return Integer.parseInt(dateFormat);
    }

    public String getStringDateFormat() {
        return this.dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Integer getTimeFormat() {
        return Integer.parseInt(timeFormat);
    }

    public String getStringTimeFormat() {
        return this.timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public Integer getTimeZone() {
        return Integer.parseInt(timeZone);
    }

    public String getStringTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isDst() {
        return isDst.equals("1");
    }

    public String getIsDst() {
        return this.isDst;
    }

    public void setIsDst(String isDst) {
        this.isDst = isDst;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setMin(String min) {
        if(min.length() < 2) {
            min = "0" + min;
        }
        this.min = min;
    }

    public boolean isNtpEnabled() {
        return timeSource.equals("0");
    }

    public void setTimeSource(String timeSource) {
        this.timeSource = timeSource;
    }

    public String getTimeSource() {
        return this.timeSource;
    }

    public String getDisplayedTime() {
        return this.month + "/" + this.day + "/" + this.year + " " + this.hour + ":" + this.min;
    }
}
