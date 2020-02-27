package com.bsuir.balashenka.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import math.Correlation;
import math.Operations;

public class WindowController {
    @FXML
    private LineChart<Number, Number> cosChart;

    @FXML
    private LineChart<Number, Number> sinChart;

    @FXML
    private LineChart<Number, Number> corChart;

    @FXML
    private LineChart<Number, Number> conChart;

    @FXML
    private LineChart<Number, Number> corFftChart;

    @FXML
    private LineChart<Number, Number> conFftChart;

    public void initialize() {
        Correlation.setValuesToLists();

        new Graph(cosChart, Operations.getCosOriginalGraph());
        new Graph(sinChart, Operations.getSinOriginalGraph());
        new Graph(conChart, Correlation.getConvolutionCorrelation(true));
        new Graph(conFftChart, Correlation.getConvolutionCorrelationFFT(true));
        new Graph(corChart, Correlation.getConvolutionCorrelation(false));
        new Graph(corFftChart, Correlation.getConvolutionCorrelationFFT(false));
    }
}
