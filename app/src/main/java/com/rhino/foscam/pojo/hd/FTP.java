package com.rhino.foscam.pojo.hd;

public class FTP {
    public String ftpAddr;
    public String ftpPort;
    public String mode;
    public String userName;
    public String password;

    public String getFtpAddr() {
        return ftpAddr;
    }

    public void setFtpAddr(String ftpAddr) {
        this.ftpAddr = ftpAddr;
    }

    public String getFtpPort() {
        return ftpPort;
    }

    public void setFtpPort(String ftpPort) {
        this.ftpPort = ftpPort;
    }

    public String getMode() {
        return mode;
    }

    public Integer getModeIndex() {
        return Integer.parseInt(this.mode);
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
