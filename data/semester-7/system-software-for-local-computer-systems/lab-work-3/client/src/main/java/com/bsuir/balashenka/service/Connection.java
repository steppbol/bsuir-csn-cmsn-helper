package com.bsuir.balashenka.service;

import java.net.SocketTimeoutException;

public interface Connection {

    boolean connect(String serverIP);

    boolean sendMessage(String data);

    void sendMessage(byte[] bytes, int length);

    String read() throws SocketTimeoutException;

    int read(byte[] buffer) throws SocketTimeoutException;

    int read(byte[] buffer, int off, int len) throws SocketTimeoutException;

    void close();

    String getRemoteSocketIpAddress();
}
