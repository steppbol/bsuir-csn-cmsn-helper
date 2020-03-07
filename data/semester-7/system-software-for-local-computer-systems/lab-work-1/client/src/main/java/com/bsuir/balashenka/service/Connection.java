package com.bsuir.balashenka.service;

import java.net.SocketAddress;

public interface Connection {
    void connect(String serverPort);

    void send(String data);

    void send(byte[] bytes, int length);

    String receive();

    int receive(byte[] buffer);

    int receive(byte[] buffer, int off, int len);

    void close();

    SocketAddress getRemoteSocketIpAddress();

    void setServerIpAddress(String serverIpAddress);
}
