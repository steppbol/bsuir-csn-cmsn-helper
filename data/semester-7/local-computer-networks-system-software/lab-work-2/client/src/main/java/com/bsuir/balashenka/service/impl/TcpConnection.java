package com.bsuir.balashenka.service.impl;

import com.bsuir.balashenka.controller.impl.ClientController;
import com.bsuir.balashenka.service.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;

public class TcpConnection implements Connection {
    private static final int SIZE_BUFF = 256;
    private static volatile TcpConnection instance;
    private int PORT;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private byte[] clientMessage;

    private TcpConnection() {
        clientMessage = new byte[SIZE_BUFF];
    }

    public static TcpConnection getInstance() {
        TcpConnection localInstance = instance;
        if (localInstance == null) {
            synchronized (TcpConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TcpConnection();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void connect(String serverIpAddress, String serverPort) {
        try {
            PORT = Integer.parseInt(serverPort);
            socket = new Socket(serverIpAddress, PORT);
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            this.initStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(String data) {
        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(byte[] bytes, int length) {
        try {
            outputStream.write(Arrays.copyOfRange(bytes, 0, length));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receive() {
        String data = null;
        try {
            int countBytes = inputStream.read(clientMessage);
            data = new String(clientMessage, 0, countBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public int receive(byte[] buffer) {
        int bytes = 0;
        try {
            bytes = inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public int receive(byte[] buffer, int off, int len) {
        int bytes = 0;
        try {
            bytes = inputStream.read(buffer, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            ClientController.getInstance().setConnection(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public SocketAddress getRemoteSocketIpAddress() {
        return socket.getRemoteSocketAddress();
    }

    private void initStream() throws IOException {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }
}
