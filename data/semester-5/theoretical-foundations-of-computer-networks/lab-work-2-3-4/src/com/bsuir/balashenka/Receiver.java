package com.bsuir.balashenka;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

public class Receiver {
    private static final int WINDOW_LENGTH = 30;
    private static final int JAM_SIGNAL_SIZE = 32;

    private SerialPort serialPort;
    private boolean isCollisionHappened;

    public boolean isCollisionHappened() {
        return isCollisionHappened;
    }

    public Receiver(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public String receive(SerialPortEvent event) {
        String data = null;
        try {
            data = serialPort.readString(event.getEventValue());
            Thread.sleep(WINDOW_LENGTH);
            serialPort.readString(JAM_SIGNAL_SIZE, 5);
            isCollisionHappened = true;
            //isCollisionHappened = isCollision();
        } catch (SerialPortTimeoutException e) {
            isCollisionHappened = false;
        } catch (SerialPortException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}

