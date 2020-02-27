package com.bsuir.balashenka.complex;

public class ComplexNumber {
    private static int minusCounter;
    private static int multiplyCounter;
    private static int plusCounter;

    private final double re;
    private final double im;

    public ComplexNumber(double real) {
        re = real;
        im = 0;
    }

    public ComplexNumber(double real, double imag) {
        re = real;
        im = imag;
    }

    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im < 0) return re + " - " + (-im) + "i";
        return re + " + " + im + "i";
    }

    public double abs() {
        return Math.hypot(re, im);
    }

    public double phase(int deflt) {
        if (re == 0) return deflt;
        return Math.atan(im / re);
    }

    public ComplexNumber plus(ComplexNumber b) {
        plusCounter++;
        ComplexNumber a = this;
        double real = a.re + b.re;
        double imag = a.im + b.im;
        return normalize(new ComplexNumber(real, imag));
    }

    public ComplexNumber minus(ComplexNumber b) {
        minusCounter++;
        ComplexNumber a = this;
        double real = a.re - b.re;
        double imag = a.im - b.im;
        return normalize(new ComplexNumber(real, imag));
    }

    public ComplexNumber times(ComplexNumber b) {
        multiplyCounter++;
        ComplexNumber a = this;
        double real = a.re * b.re - a.im * b.im;
        double imag = a.re * b.im + a.im * b.re;
        return normalize(new ComplexNumber(real, imag));
    }

    public ComplexNumber times(double alpha) {
        multiplyCounter++;
        return normalize(new ComplexNumber(alpha * re, alpha * im));
    }


    public ComplexNumber reciprocal() {
        double scale = re * re + im * im;
        return normalize(new ComplexNumber(re / scale, -im / scale));
    }

    public ComplexNumber divides(ComplexNumber b) {
        ComplexNumber a = this;
        return normalize(a.times(b.reciprocal()));
    }

    public ComplexNumber divides(double param) {
        return normalize(new ComplexNumber(this.re / param, this.im / param));
    }

    public ComplexNumber exp() {
        return normalize(new ComplexNumber(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im)));
    }

    public ComplexNumber sin() {
        return normalize(new ComplexNumber(Math.sin(re) * Math.cosh(im), Math.cos(re) * Math.sinh(im)));
    }

    public ComplexNumber cos() {
        return normalize(new ComplexNumber(Math.cos(re) * Math.cosh(im), -Math.sin(re) * Math.sinh(im)));
    }

    public ComplexNumber tan() {
        return sin().divides(cos());
    }

    public double re() {
        return re;
    }

    public double im() {
        return im;
    }

    public static int getMinusCounter() {
        return minusCounter;
    }

    public static int getMultiplyCounter() {
        return multiplyCounter;
    }


    public static int getPlusCounter() {
        return plusCounter;
    }

    public static void nullifyAllCounters() {
        minusCounter = 0;
        plusCounter = 0;
        multiplyCounter = 0;
    }

    private ComplexNumber normalize(ComplexNumber a) {
        return new ComplexNumber((Math.abs(a.re) < 0.0001)?0:a.re, (Math.abs(a.im) < 0.0001)?0:a.im);
    }
}
