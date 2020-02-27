package com.bsuir.balashenka;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.IOException;

public class Controller {

    private static SerialPort serialPort;
    private Transmitter transmitter;

    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea debugTextArea;
    @FXML
    private ComboBox<String> speedComboBox;

    public void initialize() throws IOException, SerialPortException {
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "110",
                        "4800",
                        "9600",
                        "57600",
                        "115200",
                        "128000",
                        "256000"
                );
        speedComboBox.setItems(options);
        speedComboBox.getSelectionModel().select(2);
        speedComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            int bounds;
            switch (newValue) {
                case "110":
                    bounds = 110;
                    break;
                case "4800":
                    bounds = 4800;
                    break;
                case "9600":
                    bounds = 9600;
                    break;
                case "57600":
                    bounds = 57600;
                    break;
                case "115200":
                    bounds = 115200;
                    break;
                case "128000":
                    bounds = 128000;
                    break;
                case "256000":
                    bounds = 256000;
                    break;
                default:
                    bounds = 115200;
                    break;
            }

            try {
                serialPort.setParams(bounds,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        });

        outputTextArea.setEditable(false);
        outputTextArea.setWrapText(true);
        debugTextArea.setEditable(false);
        serialPort = (SerialPort) Main.getPrimaryStage().getUserData();
        debugTextArea.appendText("Connected to " + serialPort.getPortName() + "\n\r");
        transmitter = new Transmitter(serialPort);

        inputTextArea.setOnKeyTyped(event -> {
            try {
                if (event.getCharacter().getBytes()[0] != 8)
                    transmitter.send(event.getCharacter());
                if (transmitter.isCollisionHappened())
                    debugTextArea.appendText("\n\r");
            } catch (OutOfTries e) {
                e.printStackTrace();
            }
        });

        //Устанавливаем слушатель событий и маску
        serialPort.addEventListener(new PortReader(outputTextArea, debugTextArea), SerialPort.MASK_RXCHAR);

        Main.getPrimaryStage().setOnCloseRequest(event -> {
            try {
                serialPort.closePort();
                System.out.println("Closed");
            } catch (SerialPortException e) {
                debugTextArea.appendText(e.toString());
            }
        });
    }

    private class PortReader implements SerialPortEventListener {
        private TextArea outputTextArea;
        private serial.Receiver receiver;

        public PortReader(TextArea otx, TextArea dbx) {
            receiver = new serial.Receiver(serialPort);
            outputTextArea = otx;
            debugTextArea = dbx;
        }

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                String data = receiver.receive(event);
                if ((int) data.toCharArray()[0] != 13)
                    outputTextArea.appendText(data);
                else
                    outputTextArea.appendText("\r\n");
            }
        }
    }
}
