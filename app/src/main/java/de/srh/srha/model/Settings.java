package de.srh.srha.model;

public class Settings {

    private boolean wifiOn;
    private boolean bluetoothOn;
    private boolean gpsOn;
    private boolean mobileDataOn;

    private boolean vibrateOn;
    private int ringVolume;

    public Settings(boolean wifiOn, boolean bluetoothOn, boolean gpsOn, boolean mobileDataOn, boolean vibrateOn, int ringVolume) {
        this.wifiOn = wifiOn;
        this.bluetoothOn = bluetoothOn;
        this.gpsOn = gpsOn;
        this.mobileDataOn = mobileDataOn;
        this.vibrateOn = vibrateOn;
        this.ringVolume = ringVolume;
    }

    public void setWifiOn(boolean wifiOn) { this.wifiOn = wifiOn; }

    public void setBluetoothOn(boolean bluetoothOn) { this.bluetoothOn = bluetoothOn; }

    public void setGpsOn(boolean gpsOn) { this.gpsOn = gpsOn; }

    public void setMobileDataOn(boolean mobileDataOn) { this.mobileDataOn = mobileDataOn; }

    public void setVibrateOn(boolean vibrateOn) { this.vibrateOn = vibrateOn; }

    public void setRingVolume(int ringVolume) { this.ringVolume = ringVolume; }

    public boolean isWifiOn() { return wifiOn; }

    public boolean isBluetoothOn() { return bluetoothOn; }

    public boolean isGpsOn() { return gpsOn; }

    public boolean isMobileDataOn() { return mobileDataOn; }

    public boolean isVibrateOn() { return vibrateOn; }

    public int getRingVolume() { return ringVolume; }

    public boolean equals(Settings other) {
        return this.isWifiOn() == other.isWifiOn()
                && this.isBluetoothOn() == other.isBluetoothOn()
                && this.isGpsOn() == other.isGpsOn()
                && this.isMobileDataOn() == other.isMobileDataOn()
                && this.isVibrateOn() == other.isVibrateOn()
                && this.getRingVolume() == other.getRingVolume();
    }
}
