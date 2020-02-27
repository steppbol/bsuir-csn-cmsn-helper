package com.netcracker.balashenka;

public class Token {

    private boolean tokenBit;
    private boolean monitorBit;
    private boolean addressRecognizedBit;
    private boolean frameCopiedBit;

    public Token() {

    }

    public Token(boolean tokenBit, boolean monitorBit, boolean addressRecognizedBit, boolean frameCopiedBit) {
        this.tokenBit = tokenBit;
        this.monitorBit = monitorBit;
        this.addressRecognizedBit = addressRecognizedBit;
        this.frameCopiedBit = frameCopiedBit;
    }

    @Override
    public String toString() {
        return Integer.toBinaryString(toByte() & 0xFF);
    }

    public byte toByte() {
        byte b = 0b00000000;
        if (tokenBit)
            b = (byte) (b | 0b10000000);
        if (monitorBit)
            b = (byte) (b | 0b01000000);
        if (addressRecognizedBit)
            b = (byte) (b | 0b00100000);
        if (frameCopiedBit)
            b = (byte) (b | 0b00010000);
        return b;
    }

    public void setTokenBit(boolean tokeBit) {
        this.tokenBit = tokeBit;
    }

    public void setMonitorBit(boolean monitorBit) {
        this.monitorBit = monitorBit;
    }

    public void setAddressRecognizedBit(boolean addressRecognizedBit) {
        this.addressRecognizedBit = addressRecognizedBit;
    }

    public void setFrameCopiedBit(boolean frameCopiedBit) {
        this.frameCopiedBit = frameCopiedBit;
    }

    public boolean isCopied() {
        return frameCopiedBit;
    }

}
