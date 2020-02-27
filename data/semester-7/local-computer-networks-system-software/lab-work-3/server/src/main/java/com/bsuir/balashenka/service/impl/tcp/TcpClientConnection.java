package com.bsuir.balashenka.service.impl.tcp;

import com.bsuir.balashenka.service.ClientConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeoutException;

@Slf4j
public class TcpClientConnection implements ClientConnection {

    private static final int BUFFER_SIZE = 1_000_000;

    private Socket socket;
    private BufferedInputStream inputStream;
    private BufferedOutputStream outputStream;

    TcpClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new BufferedInputStream(socket.getInputStream());
        this.outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    @Override
    public void write(String data) throws IOException {
        byte[] dataInBytes = data.getBytes();
        this.write(dataInBytes, dataInBytes.length);
    }

    @Override
    public void write(byte[] bytes, int length) throws IOException {
        outputStream.write(bytes, 0, length);
        outputStream.flush();
    }

    @Override
    public boolean sendMessage(String data) {
        try {
            outputStream.write(data.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String read(int timeout) throws IOException, TimeoutException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int readBytes = this.read(buffer, timeout);
        if (readBytes > 0) {
            return new String(buffer, 0, readBytes);
        } else {
            throw new TimeoutException("Had no data in last " + timeout + "ms.");
        }
    }

    @Override
    public int read(byte[] destination, int timeout) throws IOException {
        return this.read(destination, 0, destination.length, timeout);
    }

    @Override
    public int read(byte[] destination, int offset, int length, int timeout) throws IOException {
        long endTime = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < endTime) {
            if (inputStream.available() > 0) {
                return inputStream.read(destination, offset, length);
            }
        }
        return 0;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
        log.info("TCP Client {} was disconnected", socket.getRemoteSocketAddress());
    }

    @Override
    public String getAddress() {
        return String.valueOf(socket.getRemoteSocketAddress());
    }
}
