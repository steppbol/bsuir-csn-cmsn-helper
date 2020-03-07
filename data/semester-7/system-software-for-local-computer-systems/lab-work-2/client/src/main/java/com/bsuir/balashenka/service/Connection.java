package com.bsuir.balashenka.service;

import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public interface Connection {
    void connect(String serverIpAddress, String serverPort);

    void send(String data);

    void send(byte[] bytes, int length);

    String receive() throws SocketTimeoutException;

    int receive(byte[] buffer) throws SocketTimeoutException;

    int receive(byte[] buffer, int off, int len) throws SocketTimeoutException;

    void close();

    SocketAddress getRemoteSocketIpAddress();
}
