package com.bsuir.balashenka.service.impl;

import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.UdpPacket;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UdpConnection implements Connection {
    private static final int SO_TIMEOUT = 10;
    private static final int WINDOW_SIZE = 5;
    private static volatile UdpConnection instance;
    private int PORT;
    private DatagramSocket socket;
    private String serverIpAddress;
    private HashMap<Integer, UdpPacket> inputBuffer = new HashMap<>();

    public static UdpConnection getInstance() {
        UdpConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (TcpConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UdpConnection();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void connect(String serverIpAddress, String serverPort) {
        try {
            PORT = Integer.parseInt(serverPort);
            socket = new DatagramSocket(null);
            socket.setSoTimeout(SO_TIMEOUT);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(PORT));
            this.serverIpAddress = serverIpAddress;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String data) {
        try {
            write(data.getBytes(), data.getBytes().length);
        } catch (IOException e) {
        }
    }

    @Override
    public void send(byte[] bytes, int length) {
        try {
            write(bytes, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receive() throws SocketTimeoutException {
        byte[] buff = new byte[UdpPacket.MAX_LENGTH];
        int size = receive(buff);
        String str = new String(buff, 0, size);
        return str;
    }

    @Override
    public int receive(byte[] buffer) throws SocketTimeoutException {
        return receive(buffer, 0, buffer.length);
    }

    @Override
    public int receive(byte[] buffer, int off, int len) throws SocketTimeoutException {
        while (true) {
            UdpPacket fromCache = inputBuffer.get(UdpPacket.waitAcknowledgeNumber);
            if (fromCache != null) {
                int lenToWrite = Math.min(len, fromCache.getDataLength());
                System.arraycopy(fromCache.getData(), 0, buffer, off, lenToWrite);
                if (lenToWrite == fromCache.getDataLength()) {
                    inputBuffer.remove(UdpPacket.waitAcknowledgeNumber);
                    UdpPacket.waitAcknowledgeNumber++;
                } else {
                    fromCache.setData(Arrays.copyOfRange(fromCache.getData(), lenToWrite, fromCache.getDataLength()));
                }

                return lenToWrite;
            }
            byte[] buff = new byte[UdpPacket.MAX_LENGTH];
            ArrayList<Integer> receivedPacketsSequenceNumber = new ArrayList<>();
            while (true) {
                DatagramPacket packet = new DatagramPacket(buff, buff.length);
                try {
                    socket.receive(packet);
                    if (serverIpAddress == null) {
                        serverIpAddress = packet.getAddress().getHostAddress();
                    }
                    UdpPacket udpPacket = UdpPacket.fromStream(packet.getData());

                    if (udpPacket.getSequenceNumber() != UdpPacket.INVALID_VALUE) {
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
    public SocketAddress getRemoteSocketIpAddress() {
        return socket.getRemoteSocketAddress();
    }

    private void write(byte[] bytes, int length) throws IOException {
        ArrayList<UdpPacket> allPackets = new ArrayList<>();
        for (int i = 0; i < length; i += UdpPacket.MAX_DATA_SIZE) {
            int packetSize = Math.min(UdpPacket.MAX_DATA_SIZE, (length - i));
            UdpPacket udpPacket = new UdpPacket(bytes, i, packetSize);
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
                    if (udpPacket.getAcknowledgeNumber() != UdpPacket.INVALID_VALUE) {
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
                InetAddress.getByName(serverIpAddress),
                PORT);
        socket.send(datagramPacket);
    }
}
