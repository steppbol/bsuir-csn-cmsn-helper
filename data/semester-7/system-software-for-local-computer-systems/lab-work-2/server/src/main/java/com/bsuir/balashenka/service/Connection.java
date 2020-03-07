package com.bsuir.balashenka.service;

import java.io.IOException;

public interface Connection {
    void run(String serverPort, String serverBacklog);

    void close() throws IOException;

    void listen() throws IOException;

    void write(byte[] bytes, int length) throws IOException;

    void write(String data) throws IOException;

    String read() throws IOException;

    int read(byte[] buffer, int off, int len) throws IOException;
}
