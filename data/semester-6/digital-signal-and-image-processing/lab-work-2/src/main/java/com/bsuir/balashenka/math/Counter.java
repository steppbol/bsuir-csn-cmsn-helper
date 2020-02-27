package com.bsuir.balashenka.math;

public class Counter {
    private int count = 0;

    public void inc() {
        count++;
    }

    @Override
    public String toString() {
        return count + "";
    }
}
