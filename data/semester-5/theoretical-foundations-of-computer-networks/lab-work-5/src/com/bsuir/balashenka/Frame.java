package com.netcracker.balashenka;

public class Frame {

    private Token token;
    private byte data;
    private byte destinationAddress;
    private byte sourceAddress;

    public Frame(Token token, char data, byte destinationAddress, byte sourceAddress) {
        this.token = token;
        this.data = (byte) data;
        this.destinationAddress = destinationAddress;
        this.sourceAddress = sourceAddress;
    }

    public Frame(byte[] frame) {
        token = new Token((frame[0] & 0b10000000) != 0, (frame[0] & 0b01000000) != 0,
                (frame[0] & 0b00100000) != 0, (frame[0] & 0b00010000) != 0);
        data = frame[1];
        destinationAddress = frame[2];
        sourceAddress = frame[3];
    }

    public byte[] toByteArray() {
        byte[] cont = new byte[4];
        cont[0] = token.toByte();
        cont[1] = data;
        cont[2] = destinationAddress;
        cont[3] = sourceAddress;
        return cont;
    }

    public char getData() {
        return (char) data;
    }

    public boolean isCopied() {
        return token.isCopied();
    }

    public byte getDestinationAddress() {
        return destinationAddress;
    }

    public byte getSourceAddress() {
        return sourceAddress;
    }
}
