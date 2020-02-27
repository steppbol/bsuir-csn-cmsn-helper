package com.bsuir.balashenka.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface ClientConnection {

    void write(String data) throws IOException;

    void write(byte[] bytes, int length) throws IOException;

    boolean sendMessage(String data);

    String read(int timeout) throws IOException, TimeoutException;

    int read(byte[] destination, int timeout) throws IOException;

    int read(byte[] destination, int offset, int length, int timeout) throws IOException;

    void close() throws IOException;

    String getAddress();
}
