package com.bsuir.balashenka;

import jssc.SerialPort;

import java.util.Random;

public class Transmitter {

    private static final int WINDOW_LENGTH = 30;
    private static final int SLOT_TIME = 35;
    private static final int MAX_NUM_OF_TRIES = 10;
    private static final int JAM_SIGNAL_SIZE = 32;

    private static byte[] JAM_SIGNAL = new byte[JAM_SIGNAL_SIZE];
    private SerialPort serialPort;
    private boolean isCollisionHappened;

    Transmitter(SerialPort serialPort) {
        this.serialPort = serialPort;
        for (int i = 0; i < JAM_SIGNAL.length; i++) {
            JAM_SIGNAL[i] = 85;
        }
    }

    public boolean isCollisionHappened() {
        return isCollisionHappened;
    }

    boolean isPackageMode() {
        return false;
    }

    boolean isChannelFree() {
        long time = System.currentTimeMillis();
        time /= 1000;
        return time % 2 == 0;
    }

    boolean isCollision() {
        long time = System.currentTimeMillis();
        time /= 1000;
        return time % 2 != 0;
    }

    public void send(String symbol) throws OutOfTries {
        isCollisionHappened = false;
        int tries = 0;
        if (!isPackageMode()) {
            while (true)
                if (isChannelFree()) {
                    try {
                        serialPort.writeString(symbol);
                        Thread.sleep(WINDOW_LENGTH);
                        if (isCollision()) {
                            isCollisionHappened = true;
                            serialPort.writeBytes(JAM_SIGNAL);
                            Thread.sleep(WINDOW_LENGTH);
                            tries++;
                            if (tries >= MAX_NUM_OF_TRIES) {
                                throw new OutOfTries();
                            }
                            Random rand = new Random();
                            int k = Math.min(tries, MAX_NUM_OF_TRIES);
                            int r = rand.nextInt((int) Math.pow(2, k));
                            Thread.sleep(r * SLOT_TIME);
                        } else {
                            break;
                        }
                    } catch (OutOfTries e) {
                        throw e;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}

