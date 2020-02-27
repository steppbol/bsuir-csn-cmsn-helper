package com.bsuir.balashenka.math;

import java.util.ArrayList;
import java.util.List;

public class Fourier {

    public static List<Complex> discreteFourier(List<Complex> a, int N, int dir, Counter mul, Counter add) {
        List<Complex> result = new ArrayList<>();
        for (int k = 0; k < N; k++) {
            Complex temp = new Complex(0.0, 0.0);
            for (int n = 0; n < N; n++) {
                temp = temp.plus(a.get(n).times(new Complex(Math.cos(2 * Math.PI * k * n / N), dir * Math.sin(2 * Math.PI * k * n / N))));
                mul.inc();
                add.inc();
            }
            if (dir == -1)
                temp = temp.divides(N);
            result.add(temp);
        }
        return result;
    }

    public static List<Complex> fastFourierTime(List<Complex> a, int N, int dir, Counter mul, Counter add) {
        if (a.size() == 1) {
            return a;
        }

        Complex Wn = new Complex(Math.cos(2 * Math.PI / N), dir * Math.sin(2 * Math.PI / N));
        Complex W = new Complex(1.0, 0.0);
        List<Complex> odd = new ArrayList<>();
        List<Complex> even = new ArrayList<>();
        List<Complex> b;
        List<Complex> c;

        for (int i = 0; i < N; i++) {
            if (i % 2 == 0) {
                even.add(a.get(i));
            } else {
                odd.add(a.get(i));
            }
        }

        b = fastFourierTime(even, even.size(), dir, mul, add);
        c = fastFourierTime(odd, odd.size(), dir, mul, add);

        ArrayList<Complex> y = new ArrayList<>();
        ArrayList<Complex> z = new ArrayList<>();

        for (int j = 0; j < N / 2; j++) {
            y.add(b.get(j).plus(c.get(j).times(W)));
            z.add(b.get(j).minus(c.get(j).times(W)));
            W = W.times(Wn);
            mul.inc();
            mul.inc();
            mul.inc();
            add.inc();
            add.inc();
        }

        for (Complex temp : z) {
            y.add(temp);
        }
        return y;
    }
}