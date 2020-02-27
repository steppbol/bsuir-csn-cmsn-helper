package com.bsuir.balashenka.util;

import com.bsuir.balashenka.exception.WrongPacketFormatException;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UdpPacket implements Serializable {
    public static final int MAX_DATA_SIZE = 1024;
    public static final int MAX_LENGTH = MAX_DATA_SIZE + 3 * Integer.BYTES;
    private static final int INVALID_VALUE = -1;
    private static final int MULTIPLE_ACK = -2;

    private static final String STR_CLIENT_CONNECT_REQUEST = "hello";
    private static final String STR_CLIENT_CONNECT_RESPONSE = "hi";
    private static final String STR_SERVER_RESPONSE_ACK = "hi_ack";
    public static UdpPacket CLIENT_CONNECT_REQUEST = new UdpPacket(
            STR_CLIENT_CONNECT_REQUEST.getBytes(),
            0,
            STR_CLIENT_CONNECT_REQUEST.getBytes().length,
            INVALID_VALUE);
    public static UdpPacket CLIENT_CONNECT_RESPONSE = new UdpPacket(
            STR_CLIENT_CONNECT_RESPONSE.getBytes(),
            0,
            STR_CLIENT_CONNECT_RESPONSE.getBytes().length,
            INVALID_VALUE);
    public static UdpPacket SERVER_RESPONSE_ACK = new UdpPacket(
            STR_SERVER_RESPONSE_ACK.getBytes(),
            0,
            STR_SERVER_RESPONSE_ACK.getBytes().length,
            INVALID_VALUE);
    private int sequenceNumber;
    private int acknowledgeNumber;
    private int dataLength;
    private byte[] data;

    private UdpPacket() {
    }

    public UdpPacket(byte[] data, int off, int len, int sequenceNumber) {
        this.data = new byte[len];
        System.arraycopy(data, off, this.data, 0, len);
        this.dataLength = len;
        this.sequenceNumber = sequenceNumber;
        this.acknowledgeNumber = INVALID_VALUE;
    }

    public UdpPacket(int acknowledgeNumber) {
        this.dataLength = 0;
        this.sequenceNumber = INVALID_VALUE;
        this.acknowledgeNumber = acknowledgeNumber;
    }

    public static UdpPacket createMultipleAcknowledgePacket(List<Integer> acknowledgeNumbers) {
        UdpPacket packet = new UdpPacket();
        packet.data = new byte[acknowledgeNumbers.size() * Integer.BYTES];
        int positionInData = 0;
        for (int value : acknowledgeNumbers) {
            System.arraycopy(fromIntToBytes(value), 0, packet.data, positionInData, Integer.BYTES);
            positionInData += Integer.BYTES;
        }
        packet.dataLength = packet.data.length;
        packet.sequenceNumber = INVALID_VALUE;
        packet.acknowledgeNumber = MULTIPLE_ACK;
        return packet;
    }

    public static byte[] toStream(UdpPacket packet) {
        byte[] stream;
        stream = new byte[3 * Integer.BYTES + packet.getDataLength()];
        System.arraycopy(fromIntToBytes(packet.getSequenceNumber()), 0, stream, 0, 4);
        System.arraycopy(fromIntToBytes(packet.getAcknowledgeNumber()), 0, stream, 4, 4);
        System.arraycopy(fromIntToBytes(packet.getDataLength()), 0, stream, 8, 4);
        if (packet.getData() != null)
            System.arraycopy(packet.getData(), 0, stream, 12, packet.getDataLength());
        return stream;
    }

    private static byte[] fromIntToBytes(int num) {
        return ByteBuffer.allocate(4).putInt(num).array();
    }

    private static int fromBytesToInt(byte[] data) {
        return (ByteBuffer.wrap(data)).getInt();
    }

    public static UdpPacket fromStream(byte[] stream) {
        UdpPacket packet = new UdpPacket();
        packet.setSequenceNumber(fromBytesToInt(Arrays.copyOfRange(stream, 0, 4)));
        packet.setAcknowledgeNumber(fromBytesToInt(Arrays.copyOfRange(stream, 4, 8)));
        packet.setDataLength(fromBytesToInt(Arrays.copyOfRange(stream, 8, 12)));
        packet.setData(Arrays.copyOfRange(stream, 12, 12 + packet.getDataLength()));
        return packet;
    }

    public ArrayList<Integer> getAcknowladgeNumbers() throws WrongPacketFormatException {
        ArrayList<Integer> numbers = new ArrayList<>();
        if (data.length % Integer.BYTES == 0) {
            for (int i = 0; i < data.length; i += Integer.BYTES) {
                byte[] numberBytes = Arrays.copyOfRange(data, i, i + Integer.BYTES);
                numbers.add(fromBytesToInt(numberBytes));
            }
        } else {
            throw new WrongPacketFormatException("Packet is not multiple ack.");
        }
        return numbers;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UdpPacket)) {
            return false;
        }
        UdpPacket otherObject = (UdpPacket) obj;
        return this.getSequenceNumber() == otherObject.getSequenceNumber()
                && this.getAcknowledgeNumber() == otherObject.getAcknowledgeNumber()
                && this.getDataLength() == otherObject.getDataLength()
                && Arrays.equals(this.getData(), otherObject.getData());
    }

    public boolean isAcknowledgePacket() {
        return acknowledgeNumber != INVALID_VALUE;
    }

    public boolean isDataPacket() {
        return sequenceNumber != INVALID_VALUE;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    private void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public int getAcknowledgeNumber() {
        return acknowledgeNumber;
    }

    private void setAcknowledgeNumber(int acknowledgeNumber) {
        this.acknowledgeNumber = acknowledgeNumber;
    }

    public int getDataLength() {
        return dataLength;
    }

    private void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    private void setData(byte[] data) {
        this.data = data;
        this.dataLength = data.length;
    }
}
