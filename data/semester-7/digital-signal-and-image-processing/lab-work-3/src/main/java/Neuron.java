package main.java;

public class Neuron {
    private int state;
    private int x;
    private int y;
    private int index;

    public Neuron() {
    }

    public Neuron(int index) {
        this.index = index;
    }

    public void changeState() {
        if (state > 0) y = 1;
        if (state < 0) y = -1;
        if (state == 0) y = 0;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public int getIndex() {
        return index;
    }
}
