package com.bsuir.balashenka.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Correlation {
    public static ArrayList<Double> sinFunList = new ArrayList<>();

    public static ArrayList<Double> cosFunList = new ArrayList<>();

    private static ArrayList<Complex> sinFunListComplex = new ArrayList<>();

    private static ArrayList<Complex> cosFunListComplex = new ArrayList<>();

    private static int N = 16;

    public static void setValuesToLists() {
        Map<Double, Double> map;

        map = Operations.getSinOriginalGraph();
        initList(map, sinFunList, sinFunListComplex);
        map = Operations.getCosOriginalGraph();
        initList(map, cosFunList, cosFunListComplex);
    }

    public static Map<Double, Double> getConvolutionCorrelation(boolean flag) {
        Map<Double, Double> result = new HashMap<>();

        Counter mul = new Counter();
        Counter add = new Counter();

        if (flag) {
            for (int m = 0; m < N; m++) {
                Double Zn = 0.0;
                for (int h = 0; h < N; h++) {
                    Zn += sinFunList.get(h) * cosFunList.get((m - h + N) % N);
                    mul.inc();
                    add.inc();
                }
                result.put(2 * Math.PI * m / N, Zn / N);
            }
            System.out.println("Convolution: " + mul.toString() + " muls, " + add.toString() + " adds");
        } else {
            for (int m = 0; m < N; m++) {
                Double Zn = 0.0;
                for (int h = 0; h < N; h++) {
                    Zn += sinFunList.get(h) * cosFunList.get((m + h + N) % N);
                    mul.inc();
                    add.inc();
                }
                result.put(2 * Math.PI * m / N, Zn / N);
            }
            System.out.println("Correlation: " + mul.toString() + " muls, " + add.toString() + " adds");
        }


        return result;
    }

    public static Map<Double, Double> getConvolutionCorrelationFFT(boolean flag) {
        Counter mul = new Counter();
        Counter add = new Counter();

        List<Complex> Cx = Fourier.fastFourierTime(sinFunListComplex, N, -1, mul, add);
        List<Complex> Cy = Fourier.fastFourierTime(cosFunListComplex, N, -1, mul, add);
        List<Complex> Cz = new ArrayList<>();
        List<Complex> Z;
        Map<Double, Double> result = new HashMap<>();

        if (flag) {
            for (int i = 0; i < N; i++) {
                Cx.set(i, Cx.get(i).divides(N));
                Cy.set(i, Cy.get(i).divides(N));
                Cz.add(Cx.get(i).times(Cy.get(i)));
            }
        } else {
            for (int i = 0; i < N; i++) {
                Complex CxConjugate = Cx.get(i).conjugate();
                Cx.set(i, CxConjugate.divides(N));
                Cy.set(i, Cy.get(i).divides(N));
                Cz.add(Cx.get(i).times(Cy.get(i)));
            }
        }

        Z = Fourier.fastFourierTime(Cz, N, 1, mul, add);

        for (int i = 0; i < N; i++) {
            result.put(2 * Math.PI / N * i, Z.get(i).re());
        }

        if (flag) {
            System.out.println("Convolution with FFT: " + mul.toString() + " muls, " + add.toString() + " adds");
        } else {
            System.out.println("Correlation with FFT: " + mul.toString() + " muls, " + add.toString() + " adds");
        }
        return result;
    }

    private static void initList(Map<Double, Double> map, List<Double> funList, List<Complex> funListComplex) {
        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            funList.add((Double) entry.getValue());
            funListComplex.add(new Complex((Double) entry.getValue(), 0.0));
        }
    }
}
