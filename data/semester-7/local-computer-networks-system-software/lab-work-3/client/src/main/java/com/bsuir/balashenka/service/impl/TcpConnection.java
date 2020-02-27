package com.bsuir.balashenka.service.impl;

import com.bsuir.balashenka.controller.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class TcpConnection implements com.bsuir.balashenka.service.Connection {
    private static final int SIZE_BUFF = 256;
    private int PORT;
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private byte[] clientMessage;

    public TcpConnection(String serverPort) {
        clientMessage = new byte[SIZE_BUFF];
        PORT = Integer.parseInt(serverPort);
    }

    @Override
    public boolean connect(String serverIP) {
        try {
            socket = new Socket();
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            socket.connect(new InetSocketAddress(serverIP, PORT));

            this.initStream();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public boolean sendMessage(String data) {
        try {
            os.write(data.getBytes());
            os.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void sendMessage(byte[] bytes, int length) {
        try {
            os.write(bytes, 0, length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String read() {
        try {
            int countBytes = is.read(clientMessage);
            String data = new String(clientMessage, 0, countBytes);

            return data;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int read(byte[] buffer) {
        try {
            return is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int read(byte[] buffer, int off, int len) {
        try {
            return is.read(buffer, off, len);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void close() {
        try {
            is.close();
            os.close();

            socket.close();
            Controller.getInstance().setConnection(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getRemoteSocketIpAddress() {
        return String.valueOf(socket.getRemoteSocketAddress());
    }

    private void initStream() throws IOException {
        is = socket.getInputStream();
        os = socket.getOutputStream();
    }
}
