package com.bsuir.balashenka.service.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.parser.CommandParser;
import com.bsuir.balashenka.parser.impl.DefaultCommandParser;
import com.bsuir.balashenka.service.Connection;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import static java.net.SocketOptions.SO_TIMEOUT;

@Slf4j
public class TcpConnection implements Connection {
    private static int PORT;
    private static int BACKLOG;
    private static int SIZE_OF_BUFFER = 256;

    private static volatile TcpConnection instance;
    private CommandParser commandParser;
    private ServerSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] clientMessage;

    private TcpConnection() {
        clientMessage = new byte[SIZE_OF_BUFFER];
        commandParser = new DefaultCommandParser();
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
    public void run(String port, String backlog) {
        try {
            log.info("Server is staring...");
            PORT = Integer.parseInt(port);
            BACKLOG = Integer.parseInt(backlog);
            socket = new ServerSocket(PORT, BACKLOG);
            log.info("Server is started at {}:{}", InetAddress.getLocalHost().getHostAddress(), PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
            log.info("Server is closed. Port {} is free", PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String data) throws IOException {
        outputStream.write(data.getBytes());
    }

    @Override
    public void write(byte[] bytes, int length) throws IOException {
        outputStream.write(Arrays.copyOfRange(bytes, 0, length));
    }

    @Override
    public String read() throws IOException {
        int countBytes = inputStream.read(clientMessage);
        return new String(clientMessage, 0, countBytes);
    }

    @Override
    public int read(byte[] buffer, int off, int len) throws IOException {
        return inputStream.read(buffer, off, len);
    }

    @Override
    public void listen() throws IOException {
        log.info("Server is listening...");
        Socket client = socket.accept();
        client.setSoTimeout(SO_TIMEOUT);
        initStream(client);
        if (client.isConnected()) {
            log.info("Client {} was connected", client.getRemoteSocketAddress());
        }
        while (client.isConnected()) {
            try {
                int countOfBytes;
                if ((countOfBytes = inputStream.read(clientMessage)) == -1) {
                    break;
                }
                String commandFromClient = new String(clientMessage, 0, countOfBytes);
                log.info("Client command: {}", commandFromClient);
                Command command = commandParser.parse(commandFromClient);
                command.execute();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        this.closeClientConnection(client);
    }

    private void initStream(Socket clientSocket) throws IOException {
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
    }

    private void closeClientConnection(Socket clientSocket) throws IOException {
        inputStream.close();
        outputStream.close();
        clientSocket.close();
        log.info("Client {} was disconnected", clientSocket.getRemoteSocketAddress());
    }
}