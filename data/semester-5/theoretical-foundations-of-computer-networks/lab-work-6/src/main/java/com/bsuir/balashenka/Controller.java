import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class Controller {
    private int WINDOW_LENGTH = 30;
    private static int NUMBER_OF_PACKAGES = 4;
    private static String SYN = "SYNSYNSYNSYNSYNSYNSYNSYNSYNSYNSYN";
    private static String ACK = "ACKACKACKACKACKACKACKACKACKACKACK";
    private static byte SN = 1;
    private static byte AN = 2;
    private static byte tempSN = 0;
    private static byte tempAN = 0;

    private static SerialPort serialPort;

    @FXML
    private TextArea inputTextArea;
    @FXML
    private TextArea outputTextArea;
    @FXML
    private TextArea debugTextArea;
    @FXML
    private Button sendSYNButton;
    @FXML
    private Button sendButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button mixButton;
    @FXML
    private Button delButton;

    @FXML
    public void initialize() throws SerialPortException {
        serialPort = (SerialPort) Main.getPrimaryStage().getUserData();

        sendSYNButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                Thread.sleep(WINDOW_LENGTH);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                serialPort.writeString(SYN);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
        });

        sendButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            try {
                send(inputTextArea.getText());
            } catch (SerialPortException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        mixButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String message = inputTextArea.getText();
            if (message.length() < 10) {
                debugTextArea.appendText("Very short message!\n");
            } else {
                String[] packages = {"", "", ""};
                try {
                    for (int i = 0; i < 3; i++)
                        packages[0] += message.charAt(i);
                    for (int i = 3; i < 7; i++)
                        packages[1] += message.charAt(i);
                    for (int i = 7; i < message.length(); i++)
                        packages[2] += message.charAt(i);
                } catch (Exception e) {
                    System.out.println("Error!");
                }
                try {
                    tempSN = (byte) (NUMBER_OF_PACKAGES - 1);
                    tempAN = (byte) (NUMBER_OF_PACKAGES - 2);
                    sendWithMix(packages[2]);
                    tempSN = (byte) (NUMBER_OF_PACKAGES - 3);
                    tempAN = (byte) (NUMBER_OF_PACKAGES - 1);
                    sendWithMix(packages[0]);
                    tempSN = (byte) (NUMBER_OF_PACKAGES - 2);
                    tempAN = (byte) (NUMBER_OF_PACKAGES - 3);
                    sendWithMix(packages[1]);
                } catch (SerialPortException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        delButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String message = "Deleted package!";
            try {
                sendWithDel(message);
            } catch (SerialPortException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> outputTextArea.clear());
        serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        Main.getPrimaryStage().setOnCloseRequest(event -> {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                debugTextArea.appendText(e.toString());
            }
        });
    }

    private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                String data = "";
                try {
                    data = serialPort.readString(event.getEventValue());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
                if (data.equals(SYN)) {
                    try {
                        Thread.sleep(WINDOW_LENGTH);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        debugTextArea.appendText("SYN\n");
                        serialPort.writeString(SYN + ACK);
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                } else if (data.equals(SYN + ACK)) {
                    try {
                        Thread.sleep(WINDOW_LENGTH);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        debugTextArea.appendText("SYN, ACK\n");
                        serialPort.writeString(ACK);
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                } else if (data.equals(ACK)) {
                    debugTextArea.appendText("ACK\n");
                }

                if (!String.valueOf(data.charAt(0)).equals("0") && !data.equals(SYN) &&
                        !data.equals(SYN + ACK) && !data.equals(ACK)) {
                    try {
                        debugTextArea.appendText("Came the right package with SN = " + data.charAt(0) + " " +
                                "AN = " + data.charAt(1) + "\n");
                        for (int i = 2; i < data.length(); i++)
                            outputTextArea.appendText(String.valueOf(data.charAt(i)));
                        outputTextArea.appendText(" ");
                    } catch (Exception e) {
                        System.out.println("Error!");
                    }

                } else if (String.valueOf(data.charAt(0)).equals("0")) {
                    debugTextArea.appendText("Came the deleted package with with SN =" + data.charAt(0) + " " +
                            "AN = " + data.charAt(1) + "\n");
                    for (int i = 2; i < data.length(); i++)
                        outputTextArea.appendText(String.valueOf(data.charAt(i)));
                    outputTextArea.appendText(" ");
                }

            }
        }

    }

    private void send(String message) throws SerialPortException, InterruptedException {
        if (SN < NUMBER_OF_PACKAGES) {
            if (SN == NUMBER_OF_PACKAGES - 1) {
                Thread.sleep(WINDOW_LENGTH);
                message = String.valueOf((NUMBER_OF_PACKAGES - 1)) + String.valueOf(1) + message;
            } else {
                Thread.sleep(WINDOW_LENGTH);
                message = String.valueOf(SN) + String.valueOf(AN) + message;
            }
            SN++;
            AN++;
            serialPort.writeString(message);
        } else if (SN == NUMBER_OF_PACKAGES) {
            SN = 1;
            AN = 2;
            message = String.valueOf(SN) + String.valueOf(AN) + message;
            Thread.sleep(WINDOW_LENGTH);
            serialPort.writeString(message);
            SN++;
            AN++;
        }
    }

    private void sendWithMix(String message) throws SerialPortException, InterruptedException {
        message = String.valueOf(tempSN) + String.valueOf(tempAN) + message;
        Thread.sleep(WINDOW_LENGTH);
        serialPort.writeString(message);
    }

    private void sendWithDel(String message) throws SerialPortException, InterruptedException {
        message = String.valueOf(0) + String.valueOf(0) + message;
        Thread.sleep(WINDOW_LENGTH);
        serialPort.writeString(message);
    }
}