package com.netcracker.balashenka;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.IOException;

public class Controller {

    private SerialPort inputSerialPort;
    private SerialPort outputSerialPort;
    private int stationAddress;
    private boolean isMonitorStation;
    private Buffer buffer;
    private int SIZE_OF_MONITOR_BITS = 1;
    private int DELAY_BETWEEN_RESENDING = 300;

    @FXML private TextArea outputTextArea;
    @FXML private TextArea inputTextArea;
    @FXML private TextArea debugTextArea;
    @FXML private Button clearOutputButton;
    @FXML private Button sendMarkerButton;
    @FXML private ChoiceBox destinationAddressCB;
    @FXML private Text stAdr;

    @FXML
    public void initialize() throws IOException, SerialPortException {
        outputTextArea.setEditable(false);
        debugTextArea.setEditable(false);
        buffer = new Buffer();
        Data data = (Data) Main.getPrimaryStage().getUserData();
        inputSerialPort = data.getInputSerialPort();
        outputSerialPort = data.getOutputSerialPort();
        stationAddress = data.getStationAddress();
        isMonitorStation = data.isMonitorStation();

        stAdr.setText("Station number: " + String.valueOf(stationAddress));

        System.out.println("Station address: " + stationAddress);
        debugTextArea.appendText("Input port: " + inputSerialPort.getPortName() + "\n");
        debugTextArea.appendText("Output port: " + outputSerialPort.getPortName() + "\n");
        debugTextArea.appendText("Station address is " + stationAddress + "\n");
        destinationAddressCB.setItems(FXCollections.observableArrayList("1", "2", "3"));
        destinationAddressCB.setValue("1");
        clearOutputButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            outputTextArea.clear();
        });
        if (isMonitorStation) {
            sendMarkerButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                Token token = new Token();
                token.setTokenBit(true);
                token.setMonitorBit(true);
                token.setAddressRecognizedBit(false);
                token.setFrameCopiedBit(false);
                synchronized (outputSerialPort) {
                    try {
                        byte[] arr = new byte[1];
                        arr[0] = token.toByte();
                        outputSerialPort.writeBytes(arr);
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("Token: " + token);
            });
        } else {
            sendMarkerButton.setDisable(true);
        }
        inputTextArea.setOnKeyTyped(event -> {
            buffer.pushLast(event.getCharacter().charAt(0));
            //outputSerialPort.writeString(event.getCharacter().toString());
            System.out.println(Integer.toBinaryString(event.getCharacter().charAt(0)) + " " + buffer.size());
        });
        //Устанавливаем ивент лисенер и маску
        inputSerialPort.addEventListener(new PortReader(outputTextArea), SerialPort.MASK_RXCHAR);

        Main.getPrimaryStage().setOnCloseRequest(event -> {
            try {
                inputSerialPort.closePort();
                outputSerialPort.closePort();
                System.out.println("App closed");
            } catch (SerialPortException e) {
                debugTextArea.appendText(e.toString());
            }
        });
    }

    private class PortReader implements SerialPortEventListener {
        TextArea outputTextArea;

        PortReader(TextArea otx) {
            outputTextArea = otx;
        }

        private void writeToOutputWithDelay(byte[] data) {
            try {
                Thread.sleep(DELAY_BETWEEN_RESENDING);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (outputSerialPort) {
                try {
                    outputSerialPort.writeBytes(data);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }

        private void writeToOutputWithDelay(byte data) {
            try {
                Thread.sleep(DELAY_BETWEEN_RESENDING);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (outputSerialPort) {
                try {
                    byte[] arr = new byte[1];
                    arr[0] = data;
                    outputSerialPort.writeBytes(arr);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    //Получаем данные из порта.
                    byte[] data = inputSerialPort.readBytes(event.getEventValue());
                    System.out.println("data size: " + data.length);
                    System.out.print("data: ");
                    for (int i = 0; i < data.length; i++)
                        System.out.print(Integer.toBinaryString(data[i] & 0xFF) + " ");
                    System.out.println("\n");

                    if (data.length > SIZE_OF_MONITOR_BITS) {
                        synchronized (debugTextArea) {
                            debugTextArea.appendText("F" + "\r\n");
                        }
                        Frame frame = new Frame(data);
                        if (frame.getSourceAddress() == stationAddress) {
                            System.out.println("Is it cycle?");
                            if (frame.isCopied()) {
                                Token token1 = new Token(true, isMonitorStation,
                                        false, false);
                                writeToOutputWithDelay(token1.toByte());
                            } else {
                                writeToOutputWithDelay(frame.toByteArray());
                            }
                        } else if (frame.getDestinationAddress() == stationAddress) {
                            synchronized (outputTextArea) {
                                outputTextArea.appendText(String.valueOf(frame.getData()));
                                System.out.println("Station " + stationAddress + ": Get it!");
                                System.out.println(frame.getData());
                            }
                            Token token1 = new Token(false, isMonitorStation,
                                    true, true);
                            Frame frame1 = new Frame(token1, frame.getData(),
                                    frame.getDestinationAddress(), frame.getSourceAddress());
                            writeToOutputWithDelay(frame1.toByteArray());
                        } else {
                            writeToOutputWithDelay(data);
                        }
                    } else {
                        synchronized (debugTextArea) {
                            debugTextArea.appendText("T" + "\r\n");
                        }
                        if (buffer.isEmpty()) {
                            synchronized (outputSerialPort) {
                                writeToOutputWithDelay(data);
                            }
                        } else {
                            Token token = new Token(false, isMonitorStation,
                                    false, false);
                            Frame frame = new Frame(token, buffer.popFirst(),
                                    (byte) (destinationAddressCB.getSelectionModel().getSelectedIndex() + 1),
                                    (byte) stationAddress);
                            writeToOutputWithDelay(frame.toByteArray());
                        }
                    }
                } catch (SerialPortException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
