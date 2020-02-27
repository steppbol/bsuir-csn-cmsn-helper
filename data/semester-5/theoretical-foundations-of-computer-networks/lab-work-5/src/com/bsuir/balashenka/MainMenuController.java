package com.netcracker.balashenka;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import jssc.SerialPort;
import jssc.SerialPortException;


public class MainMenuController {
    @FXML private Text inputComText;
    @FXML private Text outputComText;
    @FXML private TextField inputComPortTF;
    @FXML private TextField outputComPortTF;
    @FXML private ChoiceBox stationAddressCB;
    @FXML private Button connectButton;
    @FXML private CheckBox monitorCheckBox;

    @FXML
    public void initialize() throws Exception {
        stationAddressCB.setItems(FXCollections.observableArrayList("1", "2", "3"));
        //stationAddressCB.setValue("1");

        stationAddressCB.valueProperty().addListener(((observable, oldValue, newValue) -> {
            if (stationAddressCB.getValue() == "1") {
                inputComPortTF.setText("COM6");
                outputComPortTF.setText("COM1");
            } else if (stationAddressCB.getValue() == "2") {
                inputComPortTF.setText("COM2");
                outputComPortTF.setText("COM3");
            } else {
                inputComPortTF.setText("COM4");
                outputComPortTF.setText("COM5");
            }
        }));

        connectButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                String inputPortString = inputComPortTF.getText();
                String outputPortString = outputComPortTF.getText();
                SerialPort inputSerialPort = null;
                SerialPort outputSerialPort = null;
                try {
                    inputSerialPort = new SerialPort(inputPortString);
                    inputSerialPort.openPort();
                    inputSerialPort.setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                } catch (SerialPortException e) {
                    inputComText.setText("Wrong port.");
                    inputComText.setFill(Color.RED);
                    return;
                }
                try {
                    outputSerialPort = new SerialPort(outputPortString);
                    outputSerialPort.openPort();
                    outputSerialPort.setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                } catch (SerialPortException e) {
                    outputComText.setText("Wrong port.");
                    outputComText.setFill(Color.RED);
                    try {
                        inputSerialPort.closePort();
                    } catch (SerialPortException e1) {
                        e1.printStackTrace();
                    }
                    return;
                }
                try {
                    Data data = new Data();
                    data.setInputSerialPort(inputSerialPort);
                    data.setOutputSerialPort(outputSerialPort);
                    data.setStationAddress(stationAddressCB.getSelectionModel().getSelectedIndex() + 1);
                    data.setMonitorStation(monitorCheckBox.isSelected());
                    Main.getPrimaryStage().setUserData(data);
                    Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("resources/connection.fxml"));
                    Main.getPrimaryStage().setScene(new Scene(root));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
