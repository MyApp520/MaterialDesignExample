package com.smile.commonlib.bean;

/**
 * Created by smile on 2019/8/19.
 */

public class BleDeviceBean {
    private String deviceName;
    private String deviceConnectStatus;
    private String deviceAddress;
    private String deviceUuid;

    public BleDeviceBean() {

    }

    public BleDeviceBean(String deviceName, String deviceConnectStatus) {
        this.deviceName = deviceName;
        this.deviceConnectStatus = deviceConnectStatus;
    }

    public BleDeviceBean(String deviceName, String deviceConnectStatus, String deviceAddress, String deviceUuid) {
        this.deviceName = deviceName;
        this.deviceConnectStatus = deviceConnectStatus;
        this.deviceAddress = deviceAddress;
        this.deviceUuid = deviceUuid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceConnectStatus() {
        return deviceConnectStatus;
    }

    public void setDeviceConnectStatus(String deviceConnectStatus) {
        this.deviceConnectStatus = deviceConnectStatus;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String deviceUuid) {
        this.deviceUuid = deviceUuid;
    }
}
