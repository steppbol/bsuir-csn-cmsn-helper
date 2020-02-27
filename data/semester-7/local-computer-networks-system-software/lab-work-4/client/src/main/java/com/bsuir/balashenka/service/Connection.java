package com.bsuir.balashenka.service;

import com.bsuir.balashenka.controller.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Connection {
    private static final int SIZE_BUFF = 256;
    private int PORT;
    private String serverIp = "192.168.1.2";
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] clientMessage;

    private Connection() {
        clientMessage = new byte[SIZE_BUFF];
    }

    public Connection(String serverIp) {
        this();
        this.serverIp = serverIp;
    }

    public void connect(String port) {
        try {
            PORT = Integer.parseInt(port);
            socket = new Socket(serverIp, PORT);
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            System.out.println("Connected to server.");
            this.initStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMessage(String data) {
        try {
            outputStream.write(data.getBytes());
            return true;
        } catch (IOException e) {
            System.out.println("Couldn't send message. " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(byte[] bytes, int length) {
        try {
            outputStream.write(Arrays.copyOfRange(bytes, 0, length));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receive() {
        try {
            int countBytes = inputStream.read(clientMessage);
            String data = new String(clientMessage, 0, countBytes);
            System.out.println("Server: " + data);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int receive(byte[] buffer) {
        try {
            return inputStream.read(buffer);
        } catch (IOException e) {
            return 0;
        }
    }

    public int receive(byte[] buffer, int off, int len) {
        int bytes = 0;
        try {
            bytes = inputStream.read(buffer, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            Controller.getInstance().setConnection(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initStream() throws IOException {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }
}
