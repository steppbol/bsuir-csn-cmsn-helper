package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.complex.ComplexNumber;
import com.bsuir.balashenka.complex.FourierFunction;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.function.Function;

import static java.lang.Math.*;

public class InterfaceController {
    public static final int N = 16;
    public static final double PERIOD = 2 * PI;
    public static final Function<Double, Double> FUNCTION = (x) -> sin(3 * x) + cos(x);

    @FXML
    Button createGraphicButton;
    @FXML
    LineChart<Number, Number> firstLineChart;
    @FXML
    LineChart<Number, Number> secondLineChart;
    @FXML
    LineChart<Number, Number> thirdLineChart;
    @FXML
    LineChart<Number, Number> fourthLineChart;
    @FXML
    LineChart<Number, Number> fifthLineChart;

    @FXML
    Label discreteCalculationsLabel;
    @FXML
    Label fastCalculationsLabel;

    public void initialize() {
        createGraphicButton.setOnMouseClicked((MouseEvent event) -> {
            GraphicCreator.showGraphic(firstLineChart, FUNCTION, PERIOD, N, "Original");

            ComplexNumber discreteFourierTransformResult[] = FourierFunction.getDiscreteFourierTransform(FUNCTION, PERIOD, N);
            System.out.println("-----Discrete-----");
            for(int i = 0; i < N; i++) {
                System.out.println("C["+i+"]="+discreteFourierTransformResult[i]);
            }
            GraphicCreator.showGraphic(secondLineChart, discreteFourierTransformResult, "Direct DFT");
            discreteCalculationsLabel.setText("+: " + ComplexNumber.getPlusCounter() + ComplexNumber.getPlusCounter()
                    + "; *: " + ComplexNumber.getMultiplyCounter());
            ComplexNumber.nullifyAllCounters();

            ComplexNumber fastFourierTransformResult[] = FourierFunction.getFastFourierTransform(FUNCTION, PERIOD, N);
            System.out.println("-----Inverse-----");
            for(int i = 0; i < N; i++) {
                System.out.println("C["+i+"]="+discreteFourierTransformResult[i]);
            }
            GraphicCreator.showGraphic(thirdLineChart, discreteFourierTransformResult, "Direct FFT");
            fastCalculationsLabel.setText("+: " + ComplexNumber.getPlusCounter() + ComplexNumber.getPlusCounter()
                    + "; *: " + ComplexNumber.getMultiplyCounter());

            double reverseFourierTransformResult[] = FourierFunction.getReverseFourierTransform(discreteFourierTransformResult, PERIOD, N);
            GraphicCreator.showGraphic(fourthLineChart, reverseFourierTransformResult, PERIOD, "Inverse DFT");

            reverseFourierTransformResult = FourierFunction.getReverseFourierTransform(fastFourierTransformResult, PERIOD, N);
            GraphicCreator.showGraphic(fifthLineChart, reverseFourierTransformResult, PERIOD, "Inverse FFT");

        });
    }
}
