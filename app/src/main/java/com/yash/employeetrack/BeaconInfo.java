package com.yash.employeetrack;

/**
 * Created by jayesh.lahare on 4/18/2018.
 */

public class BeaconInfo
{
    private String name;
    private String uuid;
    private String signal;
    private String mac;
    private String distance;
    private String power;
    private String beaconType;
    private String timeStamp;


    public BeaconInfo(String name, String uuid, String signal, String mac , String distance ,String power, String beconType, String timeStamp) {
        this.name = name;
        this.uuid = uuid;
        this.signal = signal;
        this.mac = mac;
        this.distance = distance;
        this.power = power;
        this.beaconType = beconType;
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBeaconType() {
        return beaconType;
    }

    public void setBeaconType(String beaconType) {
        this.beaconType = beaconType;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
