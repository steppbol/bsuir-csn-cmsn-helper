package com.bsuir.balashenka.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operations {
    public static List<Complex> list;
    private static int N = 16;

    public static Map<Double, Double> getCosOriginalGraph() {
        Map<Double, Double> graphHash = new HashMap<>();
        for (int i = 0; i < N; i++) {
            graphHash.put(2 * i * Math.PI / N, Math.cos(3 * 2 * i * Math.PI / N));
        }
        return graphHash;
    }

    public static Map<Double, Double> getSinOriginalGraph() {
        Map<Double, Double> graphHash = new HashMap<>();
        for (int i = 0; i < N; i++) {
            graphHash.put(2 * i * Math.PI / N, Math.sin(2 * i * Math.PI / N));
        }
        return graphHash;
    }

    public static Map<Double, Double> getDiscreteFastFourierGraph(Map<Double, Double> source, boolean flag) {
        List<Complex> temp = new ArrayList<>();
        Map<Double, Double> result = new HashMap<>();
        List<Complex> fourierList;
        Counter mul = new Counter();
        Counter add = new Counter();
        for (int i = 0; i < N; i++) {
            temp.add(new Complex(source.get(2 * i * Math.PI / N), 0.0));
        }

        if(flag){
            fourierList = Fourier.discreteFourier(temp, N, -1, mul, add);
            System.out.println("Discrete transform: add - " + add + " mul - " + mul);
            list = fourierList;
        } else {
            fourierList = Fourier.fastFourierTime(temp, N, -1, mul, add);
            System.out.println("Fast transform: add - " + add + " mul - " + mul);
            for (int i = 0; i < fourierList.size(); i++) {
                fourierList.set(i, fourierList.get(i).divides(N));
            }
            list = fourierList;
        }

        for (int i = 0; i < N; i++) {
            result.put(2 * Math.PI / N * i, fourierList.get(i).abs());
        }
        return result;
    }

    public static Map<Double, Double> getReverseFourier(int fourierType) {
        Map<Double, Double> result = new HashMap<>();
        List<Complex> fourierList = null;
        if (fourierType == 0) {
            Counter mul = new Counter();
            Counter add = new Counter();
            fourierList = Fourier.fastFourierTime(list, N, 1, mul, add);
        } else {
            Counter mul = new Counter();
            Counter add = new Counter();
            fourierList = Fourier.discreteFourier(list, N, 1, mul, add);
        }
        for (int i = 0; i < N; i++) {
            result.put(2 * Math.PI / N * i, fourierList.get(i).re());
        }
        return result;
    }

    public static Map<Double, Double> getPhaseCharacteristics(int fourierType) {
        Map<Double, Double> result = new HashMap<>();
        List<Complex> fourierList = null;
        if (fourierType == 0) {
            Counter mul = new Counter();
            Counter add = new Counter();
            fourierList = Fourier.fastFourierTime(list, N, -1, mul, add);
        } else {
            Counter mul = new Counter();
            Counter add = new Counter();
            fourierList = Fourier.discreteFourier(list, N, -1, mul, add);
        }
        for (int i = 0; i < N; i++) {
            result.put(2 * i * Math.PI / N, fourierList.get(i).phase());
        }

        return result;
    }
}
