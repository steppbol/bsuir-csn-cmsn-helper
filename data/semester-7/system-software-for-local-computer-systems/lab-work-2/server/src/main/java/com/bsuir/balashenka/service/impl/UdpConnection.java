package com.bsuir.balashenka.service.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.WrongPacketFormatException;
import com.bsuir.balashenka.parser.impl.DefaultCommandParser;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.CommandType;
import com.bsuir.balashenka.util.UdpPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class UdpConnection implements Connection {
    private static final int SO_TIMEOUT = 5;
    private static final int START_WINDOW_SIZE = 10;
    private static final int LOWEST_WINDOW_SIZE = 5;
    private static final int HIGHEST_WINDOW_SIZE = 1000;
    private static final double WINDOW_GROWTH_FACTOR = 1.5;
    private static final double WINDOW_DECLINE_FACTOR = 2;
    private static volatile UdpConnection instance;
    private int PORT;
    private int currentWindowSize = START_WINDOW_SIZE;
    private DatagramSocket socket;
    private String clientIp;

    private HashMap<Integer, UdpPacket> inputBuffer = new HashMap<>();

    public static UdpConnection getInstance() {
        UdpConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (UdpConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new UdpConnection();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void write(String data) throws IOException {
        write(data.getBytes(), data.getBytes().length);
    }

    @Override
    public void write(byte[] bytes, int length) throws IOException {
        ArrayList<UdpPacket> allPackets = new ArrayList<>();
        for (int i = 0; i < length; i += UdpPacket.MAX_DATA_SIZE) {
            int packetSize = Math.min(UdpPacket.MAX_DATA_SIZE, (length - i));
            UdpPacket udpPacket = new UdpPacket(bytes, i, packetSize);
            allPackets.add(udpPacket);
        }
        while (!allPackets.isEmpty()) {
            int windowPacketsNum = Math.min(currentWindowSize, allPackets.size());
            List<UdpPacket> windowPackets = allPackets.subList(0, windowPacketsNum);

            sendPackets(windowPackets);
            byte[] buffer = new byte[UdpPacket.MAX_LENGTH];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            boolean wasGoodSending = true;
            for (int k = 0; k < windowPacketsNum; k++) {
                try {
                    socket.receive(datagramPacket);
                    UdpPacket udpPacket = UdpPacket.fromStream(datagramPacket.getData());
                    if (udpPacket.getAcknowledgeNumber() != UdpPacket.INVALID_VALUE) {
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
    public String read() throws IOException {
        byte[] buffer = new byte[UdpPacket.MAX_LENGTH];
        int num = receive(buffer, 0, buffer.length);
        String result = null;
        if (num != -1) {
            result = new String(buffer);
            result = result.trim();
        }
        return result;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return receive(buffer, offset, length);
    }

    private int receive(byte[] buffer, int offset, int length) throws IOException {
        UdpPacket fromCache = inputBuffer.get(UdpPacket.waitAcknowledgeNumber);
        if (fromCache != null) {
            int lengthToWrite = Math.min(length, fromCache.getDataLength());
            System.arraycopy(fromCache.getData(), 0, buffer, offset, lengthToWrite);
            if (lengthToWrite == fromCache.getDataLength()) {
                inputBuffer.remove(UdpPacket.waitAcknowledgeNumber);
                UdpPacket.waitAcknowledgeNumber++;
            } else {
                fromCache.setData(Arrays.copyOfRange(fromCache.getData(), lengthToWrite, fromCache.getDataLength()));
            }

            return lengthToWrite;
        }
        byte[] tempBuffer = new byte[UdpPacket.MAX_LENGTH];
        while (true) {
            DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length);
            try {
                socket.receive(packet);
                if (clientIp == null) {
                    clientIp = packet.getAddress().getHostAddress();
                }
                UdpPacket udpPacket = UdpPacket.fromStream(packet.getData());

                if (udpPacket.getSequenceNumber() != UdpPacket.INVALID_VALUE) {
                    sendPacketAnswer(udpPacket);
                    if (udpPacket.getSequenceNumber() == UdpPacket.waitAcknowledgeNumber) {
                        int lenToWrite = Math.min(length, udpPacket.getDataLength());
                        System.arraycopy(udpPacket.getData(), 0, buffer, offset, lenToWrite);
                        if (lenToWrite == udpPacket.getDataLength()) {
                            UdpPacket.waitAcknowledgeNumber++;
                        } else {
                            udpPacket.setData(Arrays.copyOfRange(udpPacket.getData(), lenToWrite, packet.getLength()));
                            inputBuffer.put(udpPacket.getSequenceNumber(), udpPacket);
                        }

                        return lenToWrite;
                    } else {
                        inputBuffer.put(udpPacket.getSequenceNumber(), udpPacket);
                    }
                }
            } catch (SocketTimeoutException e) {
                continue;
            }
        }
    }

    @Override
    public void run(String serverPort, String serverBacklog) {
        try {
            log.info("Server is staring...");
            socket = new DatagramSocket(null);
            socket.setSoTimeout(SO_TIMEOUT);
            socket.setReuseAddress(true);
            PORT = Integer.parseInt(serverPort);
            socket.bind(new InetSocketAddress(PORT));
            log.info("Server is started at {}:{}", InetAddress.getLocalHost().getHostAddress(), PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void listen() {
        log.info("Server is listening...");
        while (true) {
            try {
                String commandFromClient = this.read();
                log.info("Client command: " + commandFromClient);
                commandFromClient = commandFromClient.trim();

                Command command = new DefaultCommandParser(CommandType.class).parse(commandFromClient);
                command.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {
        socket.close();
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
                InetAddress.getByName(clientIp),
                PORT);
        socket.send(datagramPacket);
    }
}
