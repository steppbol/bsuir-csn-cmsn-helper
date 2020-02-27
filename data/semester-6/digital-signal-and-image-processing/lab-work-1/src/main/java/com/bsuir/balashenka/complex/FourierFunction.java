package com.bsuir.balashenka.complex;

import java.util.function.Function;

import static java.lang.Math.*;

public class FourierFunction {
    public static ComplexNumber[] getDiscreteFourierTransform(Function<Double, Double> function, double period, int N) {
        ComplexNumber[] result = new ComplexNumber[N];
        for (int i = 0; i < N; i++) {
            result[i] = new ComplexNumber(0, 0);
        }

        for (int k = 0; k < N; k++) {
            for (int n = 0; n < N; n++) {
                ComplexNumber a = new ComplexNumber(cos(period * k * ((double) n) / N), sin(period * k * ((double) n / N)));
                double x = function.apply(period * ((double) n) / N);
                result[k] = result[k].plus(a.times(x));
            }
            result[k] = result[k].divides(N);
        }
        return result;
    }

    public static ComplexNumber[] getFastFourierTransform(Function<Double, Double> function, double period, int N) {
        ComplexNumber[] source = new ComplexNumber[N];
        for (int i = 0; i < N; i++) {
            source[i] = new ComplexNumber(function.apply(period * (double) i / N));
        }
        ComplexNumber[] result = getFastFourierTransformRecursive(source, period);

        for (int i = 0; i < N; i++) {
            result[i] = result[i].divides(N);
        }
        return result;
    }

    public static double[] getReverseFourierTransform(ComplexNumber[] values, double period, int N) {
        double X[] = new double[N];
        for (int i = 0; i < N; i++) {
            X[i] = 0;
        }
        for (int m = 0; m < N; m++) {
            for (int k = 0; k < N; k++) {
                ComplexNumber W2 = new ComplexNumber(cos((2 * PI) / N * (-m * k)), sin((2 * PI) / N * (-m * k)));
                X[m] += (values[k].times(W2)).re();
            }
        }
        return X;

    }

    private static ComplexNumber[] getFastFourierTransformRecursive(ComplexNumber[] sourceArray, double period) {
        if (sourceArray.length == 1) return sourceArray;
        ComplexNumber evenArray[] = new ComplexNumber[sourceArray.length / 2];
        ComplexNumber oddArray[] = new ComplexNumber[sourceArray.length / 2];
        for (int i = 0; i < sourceArray.length; i++) {
            if (i % 2 == 0) {
                evenArray[i / 2] = sourceArray[i];
            } else {
                oddArray[i / 2] = sourceArray[i];
            }
        }
        ComplexNumber[] evenResult = getFastFourierTransformRecursive(evenArray, period);
        ComplexNumber[] oddResult = getFastFourierTransformRecursive(oddArray, period);
        ComplexNumber[] result = new ComplexNumber[sourceArray.length];
        ComplexNumber Wn = new ComplexNumber(cos(period / sourceArray.length), sin(period / sourceArray.length));
        ComplexNumber w = new ComplexNumber(1);
        for (int i = 0; i < sourceArray.length / 2; i++) {
            result[i] = evenResult[i].plus(w.times(oddResult[i]));
            result[i + sourceArray.length / 2] = evenResult[i].minus(w.times(oddResult[i]));
            w = w.times(Wn);
        }
        return result;
    }
}
