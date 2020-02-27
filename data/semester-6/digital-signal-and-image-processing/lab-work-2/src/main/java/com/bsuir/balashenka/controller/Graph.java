package com.bsuir.balashenka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.Map;

public class Graph {
    private LineChart<Number, Number> graph;
    private ObservableList<XYChart.Data> data;
    private XYChart.Series series;

    public Graph(LineChart<Number, Number> lineChart, Map<Double, Double> graphHash) {
        graph = lineChart;
        graph.setCreateSymbols(false);
        graph.setLegendVisible(false);
        series = new XYChart.Series();
        data = FXCollections.observableArrayList();

        for (Object o : graphHash.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            data.add(new XYChart.Data(pair.getKey(), pair.getValue()));
        }

        series.setData(data);
        graph.getData().add(series);
    }
}
