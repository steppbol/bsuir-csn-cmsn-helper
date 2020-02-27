package main.algorithm;

public class Coordinate {
    private double x;
    private double y;
    private double absolute;

    public Coordinate(double x, double y, double absolute) {
        this.x = x;
        this.y = y;
        this.absolute = absolute;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAbsolute() {
        return absolute;
    }
}
