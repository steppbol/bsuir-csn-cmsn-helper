package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.complex.ComplexNumber;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.function.Function;

public class GraphicCreator {

    public static void showGraphic(LineChart<Number, Number> lineChart, ComplexNumber[] values, String title) {
        lineChart.setTitle(title);
        lineChart.setLegendVisible(false);
        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data(values[i].phase(i), values[i].abs()));
        }
        lineChart.getData().add(series);
    }

    public static void showGraphicRe(LineChart<Number, Number> lineChart, ComplexNumber[] values, String title) {
        lineChart.setTitle(title);
        lineChart.setLegendVisible(false);
        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data(i, values[i].re()));
        }
        lineChart.getData().add(series);
    }

    public static void showGraphicIm(LineChart<Number, Number> lineChart, ComplexNumber[] values, String title) {
        lineChart.setTitle(title);
        lineChart.setLegendVisible(false);
        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < values.length; i++) {
            series.getData().add(new XYChart.Data(i, values[i].im()));
        }
        lineChart.getData().add(series);
    }

    public static void showGraphic(LineChart<Number, Number> lineChart, double[] ys, double period, String title) {
        lineChart.setTitle(title);
        lineChart.setLegendVisible(false);
        double[] xs = new double[ys.length];
        for (int i = 0; i < xs.length; i++) {
            xs[i] = period * (double) i / xs.length;
        }

        XYChart.Series series = new XYChart.Series();
        for (int i = 0; i < ys.length; i++) {
            series.getData().add(new XYChart.Data(xs[i], ys[i]));
        }
        lineChart.getData().add(series);
    }

    public static void showGraphic(LineChart<Number, Number> lineChart,
                                   Function<Double, Double> function, double period, int N, String title) {
        lineChart.setTitle(title);
        lineChart.setLegendVisible(false);
        XYChart.Series series = new XYChart.Series();

        for (int i = 0; i < N; i++) {
            series.getData().add(new XYChart.Data(period * (double) i / N,
                    function.apply(period * (double) i / N)));
        }
        lineChart.getData().add(series);
    }
}
