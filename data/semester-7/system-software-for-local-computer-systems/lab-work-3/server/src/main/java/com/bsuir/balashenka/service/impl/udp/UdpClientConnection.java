package com.bsuir.balashenka.service.impl.udp;

import com.bsuir.balashenka.exception.WrongPacketFormatException;
import com.bsuir.balashenka.service.ClientConnection;
import com.bsuir.balashenka.util.UdpPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
public class UdpClientConnection implements ClientConnection {
    private static final int SO_TIMEOUT = 5;
    private static final int START_WINDOW_SIZE = 150;
    private static final int LOWEST_WINDOW_SIZE = 50;
    private static final int HIGHEST_WINDOW_SIZE = 200;
    private static final double WINDOW_GROWTH_FACTOR = 1.5;
    private static final double WINDOW_DECLINE_FACTOR = 2;
    private int currentWindowSize = START_WINDOW_SIZE;
    private DatagramSocket socket;
    private SocketAddress clientAddress;
    private HashMap<Integer, UdpPacket> inputBuffer = new HashMap<>();
    private int waitAcknowledgeNumber = 1;
    private int nextPacketSequenceNumber = 1;

    public UdpClientConnection(DatagramSocket socket, SocketAddress clientAddress) {
        this.socket = socket;
        this.clientAddress = clientAddress;
    }

    @Override
    public void write(String data) throws IOException {
        this.write(data.getBytes(), data.getBytes().length);
    }

    @Override
    public boolean sendMessage(String data) {
        try {
            write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void write(byte[] bytes, int length) throws IOException {
        ArrayList<UdpPacket> allPackets = new ArrayList<>();
        for (int i = 0; i < length; i += UdpPacket.MAX_DATA_SIZE) {
            int packetSize = Math.min(UdpPacket.MAX_DATA_SIZE, (length - i));
            UdpPacket udpPacket = new UdpPacket(bytes, i, packetSize, nextPacketSequenceNumber++);
            allPackets.add(udpPacket);
        }
        while (!allPackets.isEmpty()) {
            int windowPacketsNum = Math.min(currentWindowSize, allPackets.size());
            List<UdpPacket> windowPackets = allPackets.subList(0, windowPacketsNum);

            sendPackets(windowPackets);
            byte[] buff = new byte[UdpPacket.MAX_LENGTH];
            DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length);
            boolean wasGoodSending = true;
            for (int k = 0; k < windowPacketsNum; k++) {
                try {
                    socket.receive(datagramPacket);
                    UdpPacket udpPacket = UdpPacket.fromStream(datagramPacket.getData());
                    if (udpPacket.isAcknowledgePacket()) {
                        ArrayList<Integer> ackNumbers = udpPacket.getAcknowladgeNumbers();
                        windowPackets.removeIf((UdpPacket p) -> ackNumbers.contains(p.getSequenceNumber()));
                    } else {
                        inputBuffer.put(udpPacket.getSequenceNumber(), udpPacket);
                    }
                } catch (WrongPacketFormatException | SocketTimeoutException e) {
                    wasGoodSending = false;
                    continue;
                }
            }
            if (wasGoodSending) {
                currentWindowSize *= WINDOW_GROWTH_FACTOR;
                if (currentWindowSize > HIGHEST_WINDOW_SIZE) {
                    currentWindowSize = HIGHEST_WINDOW_SIZE;
                }
            } else {
                currentWindowSize /= WINDOW_DECLINE_FACTOR;
                if (currentWindowSize < LOWEST_WINDOW_SIZE) {
                    currentWindowSize = LOWEST_WINDOW_SIZE;
                }
            }
        }
    }

    @Override
    public String read(int timeout) throws IOException, TimeoutException {
        byte[] buff = new byte[UdpPacket.MAX_LENGTH];
        int wasRead = this.read(buff, timeout);
        String result;
        if (wasRead != 0) {
            result = new String(buff, 0, wasRead);
            result = result.trim();
        } else {
            throw new TimeoutException("Had no data in last " + timeout + "ms.");
        }
        return result;
    }

    @Override
    public int read(byte[] destination, int timeout) throws IOException {
        return this.read(destination, 0, destination.length, timeout);
    }

    @Override
    public int read(byte[] destination, int offset, int length, int timeout) throws IOException {
        UdpPacket fromCache = inputBuffer.get(waitAcknowledgeNumber);
        if (fromCache != null) {
            int lenToWrite = Math.min(length, fromCache.getDataLength());
            System.arraycopy(fromCache.getData(), 0, destination, offset, lenToWrite);
            if (lenToWrite == fromCache.getDataLength()) {
                inputBuffer.remove(waitAcknowledgeNumber);
                waitAcknowledgeNumber++;
            } else {
                inputBuffer.put(waitAcknowledgeNumber, new UdpPacket(fromCache.getData(),
                        lenToWrite,
                        fromCache.getDataLength() - lenToWrite,
                        waitAcknowledgeNumber));
            }

            return lenToWrite;
        }
        int numOfAttempts = timeout / SO_TIMEOUT;
        byte[] buff = new byte[UdpPacket.MAX_LENGTH];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            try {
                socket.receive(packet);
                UdpPacket udpPacket = UdpPacket.fromStream(packet.getData());

                if (udpPacket.isDataPacket()) {
                    sendPacketAnswer(udpPacket);
                    if (udpPacket.getSequenceNumber() == waitAcknowledgeNumber) {
                        int lenToWrite = Math.min(length, udpPacket.getDataLength());
                        System.arraycopy(udpPacket.getData(), 0, destination, offset, lenToWrite);
                        if (lenToWrite == udpPacket.getDataLength()) {
                            waitAcknowledgeNumber++;
                        } else {
                            inputBuffer.put(udpPacket.getSequenceNumber(),
                                    new UdpPacket(udpPacket.getData(),
                                            lenToWrite,
                                            udpPacket.getDataLength() - lenToWrite,
                                            udpPacket.getSequenceNumber()));
                        }

                        return lenToWrite;
                    } else {
                        inputBuffer.put(udpPacket.getSequenceNumber(), udpPacket);
                    }
                }
            } catch (SocketTimeoutException e) {
                numOfAttempts--;
                if (numOfAttempts < 0) {
                    return 0;
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
        log.info("UDP Client {} was disconnected", socket.getRemoteSocketAddress());
    }

    @Override
    public String getAddress() {
        return String.valueOf(clientAddress);
    }

    private void sendPackets(List<UdpPacket> packets) throws IOException {
        for (UdpPacket packet : packets) {
            sendUdpPacket(packet);
        }
    }

    private void sendPacketAnswer(UdpPacket packet) throws IOException {
        UdpPacket answer = new UdpPacket(packet.getSequenceNumber());
        sendUdpPacket(answer);
    }

    private void sendUdpPacket(UdpPacket packet) throws IOException {
        byte[] dataToSend = UdpPacket.toStream(packet);
        DatagramPacket datagramPacket = new DatagramPacket(
                dataToSend,
                dataToSend.length,
                clientAddress);
        socket.send(datagramPacket);
    }
}
