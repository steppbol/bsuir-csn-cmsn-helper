package com.bsuir.balashenka;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static java.lang.Thread.sleep;

public class GUI {
    private static JLabel responseLabel;
    private static JLabel infoLabel;
    private static JButton sendButton;
    private static JButton clearButton;
    private static JButton connectButton;
    private static JButton disconnectButton;
    private static JButton refreshButton;
    private static JTextArea textField;
    private static Serial serialPort;
    private static JRadioButton crcCodingRadioButton;
    private static JComboBox<String> speedBox;
    private static JComboBox<String> portsBox;

    public void init() {
        responseLabel = new JLabel("Response: ");
        infoLabel = new JLabel("Info: ");
        sendButton = new JButton("Send");
        clearButton = new JButton("Clear");
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        refreshButton = new JButton("Refresh");
        textField = new JTextArea();
        textField.setLineWrap(true);

        JFrame frame = new JFrame("COM-Port Control");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 300);
        JPanel allPanel = new JPanel();
        allPanel.setLayout(new BorderLayout());
        allPanel.add(textField, BorderLayout.CENTER);

        String[] ports = SerialPortList.getPortNames();
        portsBox = new JComboBox<String>(ports);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(1, 1));
        leftPanel.add(portsBox);
        allPanel.add(leftPanel, BorderLayout.WEST);

        String[] bounds = {"110", "300", "600", "1200", "4800", "9600", "14400", "19200", "38400",
                "57600", "115200", "128000", "256000"};
        speedBox = new JComboBox<String>(bounds);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(1, 1));
        rightPanel.add(speedBox);
        allPanel.add(rightPanel, BorderLayout.EAST);

        JPanel downPanel = new JPanel();
        downPanel.setLayout(new GridLayout(1, 4));
        downPanel.add(clearButton);
        downPanel.add(refreshButton);
        downPanel.add(sendButton);
        downPanel.add(disconnectButton);
        downPanel.add(connectButton);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(3, 1));
        southPanel.add(downPanel);
        southPanel.add(infoLabel);
        southPanel.add(responseLabel);
        allPanel.add(southPanel, BorderLayout.SOUTH);

        frame.add(allPanel);
        frame.setVisible(true);

        if (ports.length == 0) {
            sendButton.setEnabled(false);
        }

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { clearAction(); }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendAction();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAction();
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectAction();
            }
        });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectAction();
            }
        });

    }

    private static class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR() && event.getEventValue() > 0) {
                byte[] readBytes;
                readBytes = serialPort.read(event.getEventValue(), false);
                if (readBytes != null) {
                    String result = new String(readBytes);
                    responseLabel.setText(responseLabel.getText() + result);
                    Calendar calendar = new GregorianCalendar();
                    infoLabel.setText("Info: " +
                            Long.toString(calendar.getTimeInMillis()) + " - reading...");
                }
            }
        }
    }

    private void clearAction() {
        responseLabel.setText("Response: ");
        infoLog("");
    }

    private void sendAction() {

        if (serialPort == null) {
            infoLog("Port doesn't connect.");
            infoLabel.setBackground(Color.RED);
            infoLabel.setOpaque(true);
            return;
        }

        String text = textField.getText();
        for (int i = 0; i < text.length(); i++) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                serialPort.write(String.valueOf(text.charAt(i)).getBytes(), false);
        }

    }

    private void refreshAction() {
        String[] ports = SerialPortList.getPortNames();
        portsBox.removeAllItems();
        for (String s : ports) {
            portsBox.addItem(s);
        }
        if (ports.length == 0) {
            sendButton.setEnabled(false);
        } else {
            sendButton.setEnabled(true);
        }
        infoLog("Port list was refreshed.");
    }

    private void connectAction() {
        if (serialPort != null) {
            serialPort.close();
        }

        serialPort = new Serial(portsBox.getSelectedItem().toString());
        boolean openedFlag = serialPort.open();
        serialPort.setParams(Integer.valueOf(speedBox.getSelectedItem().toString()),
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);

        serialPort.addListener(new PortReader());
        if (openedFlag) {
            infoLog(portsBox.getSelectedItem() + " was connected.");
            infoLabel.setBackground(Color.GREEN);
            infoLabel.setOpaque(true);
        } else {
            infoLog(portsBox.getSelectedItem() + " can't connected.");
            infoLabel.setBackground(null);
            infoLabel.setOpaque(false);
        }
    }

    private void disconnectAction() {
        if (serialPort != null) {
            serialPort.close();
            infoLog(portsBox.getSelectedItem() + " was closed.");
            serialPort = null;
        } else {
            infoLog(portsBox.getSelectedItem() + " not connect.");
            infoLabel.setBackground(Color.RED);
            infoLabel.setOpaque(true);
        }
    }

    private void infoLog(String message) {
        if (infoLabel != null) {
            infoLabel.setText("Info: " + message);
        }
        System.out.println(message);
    }
}
