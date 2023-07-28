package ru.scoltech.openran.speedtest.util;

public class Pipeline {

    private String name;
    private String devicePrefs;
    private String serverPrefs;

    public Pipeline(String name, String devicePrefs, String serverPrefs){
        this.name = name;
        this.devicePrefs = devicePrefs;
        this.serverPrefs = serverPrefs;
    }

    public String getName(){
        return name;
    }

    public String getDevicePrefs() {
        return devicePrefs;
    }

    public String getServerPrefs() {
        return serverPrefs;
    }
}
