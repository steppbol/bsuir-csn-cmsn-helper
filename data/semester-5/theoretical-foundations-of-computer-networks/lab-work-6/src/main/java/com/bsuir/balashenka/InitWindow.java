import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import jfxtras.styles.jmetro8.JMetro;
import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class InitWindow {
    @FXML
    private Text outputText;
    @FXML
    private Button connectButton;
    @FXML
    private ComboBox<String> portComboBox;

    public void initialize() {
        ObservableList<String> options = FXCollections.observableArrayList(SerialPortList.getPortNames());
        portComboBox.setItems(options);

        connectButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent mouseEvent) {

                try {
                    SerialPort serialPort = new SerialPort(portComboBox.getValue());
                    serialPort.openPort();
                    serialPort.setParams(SerialPort.BAUDRATE_9600,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                            SerialPort.FLOWCONTROL_RTSCTS_OUT);
                    Main.getPrimaryStage().setUserData(serialPort);
                    Parent root = FXMLLoader.load(getClass().getResource("main_window.fxml"));
                    new JMetro(JMetro.Style.DARK).applyTheme(root);
                    Main.getPrimaryStage().setScene(new Scene(root));
                } catch (SerialPortException e) {
                    outputText.setText("Wrong port, try again.");
                    outputText.setFill(Color.RED);
                } catch (Exception e) {
                    outputText.setText(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }
}
