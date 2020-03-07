package com.bsuir.balashenka.service;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.parser.impl.CommandParser;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

@Slf4j
public class DefaultServerConnection {
    private static final int SIZE_BUFF = 256;
    private static volatile DefaultServerConnection instance;
    private int PORT;
    private ServerSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] clientMessage;

    public DefaultServerConnection() {
        clientMessage = new byte[SIZE_BUFF];
    }

    public void write(String data) throws IOException {
        outputStream.write(data.getBytes());
    }

    public void write(byte[] bytes, int length) throws IOException {
        outputStream.write(Arrays.copyOfRange(bytes, 0, length));
    }

    public String read() throws IOException {
        int countBytes = inputStream.read(clientMessage);

        return new String(clientMessage, 0, countBytes);
    }

    public int read(byte[] buffer, int off, int len) throws IOException {
        return inputStream.read(buffer, off, len);
    }

    public void open(String port, String backlog) {
        try {
            log.info("Server is staring...");
            PORT = Integer.parseInt(port);
            int BACKLOG = Integer.parseInt(backlog);
            socket = new ServerSocket(PORT, BACKLOG);
            log.info("Server is started at {}:{}", InetAddress.getLocalHost().getHostAddress(), PORT);
        } catch (IOException e) {
            log.error("Couldn't listen to port {}", PORT);
            e.printStackTrace();
        }
    }

    public Socket accept() {
        Socket client = null;
        try {
            client = socket.accept();
            log.info("Client {} was connected", client.getRemoteSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return client;
    }

    public void listen(Socket client, int index) {
        try {
            this.initStream(client);
            while (true) {
                try {
                    int countBytes;
                    if ((countBytes = inputStream.read(clientMessage)) == -1) {
                        break;
                    }
                    String cmd = new String(clientMessage, 0, countBytes);
                    log.debug("Client {} command: {}", client.getRemoteSocketAddress(), cmd);
                    Command command = new CommandParser().handle(cmd);
                    command.execute(this);
                } catch (IOException e) {
                    log.error("Client {} stopped working with server", client.getRemoteSocketAddress());
                    e.printStackTrace();
                    break;
                } catch (WrongCommandFormatException | CommandNotFoundException e) {
                    e.printStackTrace();
                }
            }

            this.closeClientConnection(client, index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    private void initStream(Socket socket) throws IOException {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    private void closeClientConnection(Socket client, int index) throws IOException {
        inputStream.close();
        outputStream.close();
        client.close();
        log.info("Client {} with index {} has been disconnected", client.getRemoteSocketAddress(), index);
    }
}
