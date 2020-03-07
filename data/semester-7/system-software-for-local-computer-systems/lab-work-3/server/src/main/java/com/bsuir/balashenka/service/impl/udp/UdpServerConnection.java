package com.bsuir.balashenka.service.impl.udp;

import com.bsuir.balashenka.util.UdpPacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;

@Slf4j
public class UdpServerConnection {
    private static final int SO_TIMEOUT = 1;
    private static final int RECEIVE_BUFFER_SIZE = 1_000_000;
    private int PORT;

    public UdpServerConnection(String port) {
        PORT = Integer.parseInt(port);
    }

    public UdpClientConnection open(int timeout) throws IOException, TimeoutException {
        DatagramSocket socket = createBoundedSocket(PORT);

        DatagramPacket clientRequestPacket = receiveDatagram(socket, timeout);

        UdpClientConnection udpClientConnection = null;
        if (clientRequestPacket != null) {
            SocketAddress clientAddress = clientRequestPacket.getSocketAddress();
            UdpPacket helloPacket = UdpPacket.fromStream(clientRequestPacket.getData());

            if (UdpPacket.CLIENT_CONNECT_REQUEST.equals(helloPacket)) {
                sendUdpPacket(socket, clientAddress, UdpPacket.CLIENT_CONNECT_RESPONSE);

                DatagramPacket responseAck;
                while (true) {
                    responseAck = receiveDatagram(socket, 1);
                    if (responseAck != null) break;
                    sendUdpPacket(socket, clientAddress, UdpPacket.CLIENT_CONNECT_RESPONSE);
                }
                udpClientConnection = new UdpClientConnection(socket, clientAddress);
                log.info("UDP Client {} is connected", udpClientConnection.getAddress());
            }
        }
        if (udpClientConnection == null) {
            socket.close();
            throw new TimeoutException("Had no client in last " + timeout + "ms");
        }
        return udpClientConnection;
    }

    private DatagramSocket createBoundedSocket(int port) throws IOException {
        DatagramSocket socket;
        socket = new DatagramSocket(null);
        socket.setSoTimeout(SO_TIMEOUT);
        socket.setReuseAddress(true);
        socket.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
        socket.bind(new InetSocketAddress(port));
        return socket;
    }

    private void sendUdpPacket(DatagramSocket socket, SocketAddress clientAddress, UdpPacket packet) throws IOException {
        byte[] dataToSend = UdpPacket.toStream(packet);
        DatagramPacket datagramPacket = new DatagramPacket(
                dataToSend,
                dataToSend.length,
                clientAddress);
        socket.send(datagramPacket);
    }

    private DatagramPacket receiveDatagram(DatagramSocket socket, int numberOfAttempts) throws IOException {
        byte[] buff = new byte[255];
        DatagramPacket receivedPacket = new DatagramPacket(buff, buff.length);
        while (numberOfAttempts > 0) {
            try {
                socket.receive(receivedPacket);
            } catch (SocketTimeoutException e) {
                numberOfAttempts--;
                continue;
            }
            break;
        }
        if (numberOfAttempts > 0) {
            return receivedPacket;
        } else {
            return null;
        }
    }

    public String getAddress() {
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress() + ":" + PORT;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return address;
    }

    public void close() throws IOException {
    }
}
