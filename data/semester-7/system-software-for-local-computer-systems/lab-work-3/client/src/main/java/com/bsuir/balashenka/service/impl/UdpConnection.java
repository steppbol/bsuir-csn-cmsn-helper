package com.bsuir.balashenka.service.impl;

import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.UdpPacket;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UdpConnection implements Connection {
    private static final int SO_TIMEOUT = 10;
    private static final int WINDOW_SIZE = 5;
    private static final int PORT = 8033;
    private DatagramSocket socket;
    private String serverIp;
    private int waitAcknowledgeNumber = 1;
    private int nextPacketSequenceNumber = 1;
    private HashMap<Integer, UdpPacket> inputBuffer = new HashMap<>();

    @Override
    public boolean connect(String serverIp) {
        try {
            socket = new DatagramSocket(null);
            socket.setSoTimeout(SO_TIMEOUT);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(PORT));
            this.serverIp = serverIp;

            sendUdpPacket(UdpPacket.CLIENT_CONNECT_REQUEST);

            byte[] buff = new byte[UdpPacket.MAX_LENGTH];
            DatagramPacket receivedPacket = new DatagramPacket(buff, buff.length);
            while (true) {
                try {
                    socket.receive(receivedPacket);
                    break;
                } catch (SocketTimeoutException ignored) {
                    sendUdpPacket(UdpPacket.CLIENT_CONNECT_REQUEST);
                }
            }
            UdpPacket connectResponse = UdpPacket.fromStream(receivedPacket.getData());

            if (UdpPacket.CLIENT_CONNECT_RESPONSE.equals(connectResponse)) {
                sendUdpPacket(UdpPacket.SERVER_RESPONSE_ACK);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendMessage(byte[] bytes, int length) {
        try {
            write(bytes, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendMessage(String data) {
        try {
            write(data.getBytes(), data.getBytes().length);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String read() throws SocketTimeoutException {
        byte[] buff = new byte[UdpPacket.MAX_LENGTH];
        int size = read(buff);
        String str = new String(buff, 0, size);
        return str;
    }

    @Override
    public int read(byte[] buffer) throws SocketTimeoutException {
        return read(buffer, 0, buffer.length);
    }

    @Override
    public int read(byte[] buffer, int off, int len) throws SocketTimeoutException {
        while (true) {
            UdpPacket fromCache = inputBuffer.get(waitAcknowledgeNumber);
            if (fromCache != null) {
                int lenToWrite = Math.min(len, fromCache.getDataLength());
                System.arraycopy(fromCache.getData(), 0, buffer, off, lenToWrite);
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
            byte[] buff = new byte[UdpPacket.MAX_LENGTH];
            ArrayList<Integer> receivedPacketsSequenceNumber = new ArrayList<>();
            while (true) {
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                try {
                    socket.receive(packet);
                    if (serverIp == null) {
                        serverIp = packet.getAddress().getHostAddress();
                    }

                    UdpPacket udpPacket = UdpPacket.fromStream(packet.getData());

                    if (udpPacket.isDataPacket()) {
                        //sendPacketAnswer(udpPacket);
                        receivedPacketsSequenceNumber.add(udpPacket.getSequenceNumber());
                        inputBuffer.put(udpPacket.getSequenceNumber(), udpPacket);
                    }
                } catch (SocketTimeoutException e) {
                    UdpPacket ansPacket = UdpPacket.createMultipleAcknowledgePacket(receivedPacketsSequenceNumber);
                    try {
                        sendUdpPacket(ansPacket);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                } catch (IOException e) {
                    return -1;
                }
            }
        }
    }

    @Override
    public void close() {

    }

    @Override
    public String getRemoteSocketIpAddress() {
        return String.valueOf(socket.getRemoteSocketAddress());
    }


    private void write(byte[] bytes, int length) throws IOException {
        ArrayList<UdpPacket> allPackets = new ArrayList<>();
        for (int i = 0; i < length; i += UdpPacket.MAX_DATA_SIZE) {
            int packetSize = Math.min(UdpPacket.MAX_DATA_SIZE, (length - i));
            UdpPacket udpPacket = new UdpPacket(bytes, i, packetSize, nextPacketSequenceNumber++);
            allPackets.add(udpPacket);
        }
        while (!allPackets.isEmpty()) {
            int windowPacketsNum = Math.min(WINDOW_SIZE, allPackets.size());
            List<UdpPacket> windowPackets = allPackets.subList(0, windowPacketsNum);

            sendPackets(windowPackets);
            byte[] buff = new byte[UdpPacket.MAX_LENGTH];
            DatagramPacket datagramPacket = new DatagramPacket(buff, buff.length);
            int packetsNum = windowPackets.size();
            for (int k = 0; k < packetsNum; k++) {
                try {
                    socket.receive(datagramPacket);
                    UdpPacket udpPacket = UdpPacket.fromStream(datagramPacket.getData());

                    if (udpPacket.isAcknowledgePacket()) {
                        windowPackets.removeIf((UdpPacket p) -> p.getSequenceNumber() == udpPacket.getAcknowledgeNumber());
                    } else {
                        inputBuffer.put(udpPacket.getSequenceNumber(), udpPacket);
                    }
                } catch (SocketTimeoutException e) {
                    continue;
                }
            }
        }
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

    private void sendPacketAnswer(int sequenceNumber) throws IOException {
        UdpPacket answer = new UdpPacket(sequenceNumber);
        sendUdpPacket(answer);
    }

    private void sendUdpPacket(UdpPacket packet) throws IOException {
        byte[] dataToSend = UdpPacket.toStream(packet);
        DatagramPacket datagramPacket = new DatagramPacket(
                dataToSend,
                dataToSend.length,
                InetAddress.getByName(serverIp),
                PORT);
        socket.send(datagramPacket);
    }
}
