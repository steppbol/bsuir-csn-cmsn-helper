package com.netcracker.balashenka;

import java.util.LinkedList;

public class Buffer {
    private LinkedList<Character> buffer;

    public Buffer() {
        buffer = new LinkedList<>();
    }

    public synchronized void pushLast(char c) {
        if (buffer != null) {
            buffer.addLast(c);
        }
    }

    public synchronized char popFirst() {
        if (buffer != null && !buffer.isEmpty()) {
            return buffer.pollFirst();
        }
        return 0;
    }

    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }

    public synchronized int size() {
        return buffer.size();
    }
}
