package com.bsuir.balashenka.service.impl.tcp;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeoutException;

@Slf4j
public class TcpServerConnection {
    private static final int TIMEOUT = 1;
    private int PORT;
    private int BACKLOG;

    private ServerSocket serverSocket;

    public TcpServerConnection(String port, String backlog) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.setSoTimeout(TIMEOUT);
        serverSocket.setReuseAddress(true);
        PORT = Integer.parseInt(port);
        BACKLOG = Integer.parseInt(backlog);
        serverSocket.bind(new InetSocketAddress(PORT), BACKLOG);
    }

    public TcpClientConnection open(int timeout) throws IOException, TimeoutException {
        TcpClientConnection clientConnection = null;
        for (int i = 0; i < timeout; i++) {
            try {
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                clientConnection = new TcpClientConnection(clientSocket);
                log.info("TCP Client {} is connected", clientSocket.getRemoteSocketAddress());
            } catch (SocketTimeoutException ignored) {
            }
        }
        if (clientConnection == null) {
            throw new TimeoutException("Had no client in last " + timeout + "ms");
        }
        return clientConnection;
    }

    public void close() throws IOException {
        serverSocket.close();
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
}
