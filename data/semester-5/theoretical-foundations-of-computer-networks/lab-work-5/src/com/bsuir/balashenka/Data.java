package com.netcracker.balashenka;

import jssc.SerialPort;

public class Data {

    private SerialPort inputSerialPort;
    private SerialPort outputSerialPort;
    private int stationAddress;
    private boolean isMonitorStation;

    public SerialPort getInputSerialPort() {
        return inputSerialPort;
    }

    public void setInputSerialPort(SerialPort inputSerialPort) {
        this.inputSerialPort = inputSerialPort;
    }

    public SerialPort getOutputSerialPort() {
        return outputSerialPort;
    }

    public void setOutputSerialPort(SerialPort outputSerialPort) {
        this.outputSerialPort = outputSerialPort;
    }

    public int getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(int stationAddress) {
        this.stationAddress = stationAddress;
    }

    public boolean isMonitorStation() {
        return isMonitorStation;
    }

    public void setMonitorStation(boolean monitorStation) {
        isMonitorStation = monitorStation;
    }
}
