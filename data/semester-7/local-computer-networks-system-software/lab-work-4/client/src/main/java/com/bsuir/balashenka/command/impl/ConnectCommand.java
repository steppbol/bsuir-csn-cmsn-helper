package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.AvailableTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class ConnectCommand extends BaseCommand {
    public static final String SERVER_IP_REGEX = "^(\\d{1,3}\\.){3}\\d{1,3}$";

    public ConnectCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            String firstKey = String.valueOf(getTokens().keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            switch (currentToken) {
                case IP:
                    executeConnect();
                    break;
                case HELP:
                    executeHelp();
                    break;
            }
        } catch (WrongCommandFormatException | AvailableTokenNotPresentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new ConnectCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getTokens();

        if (tokens.size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }

        if (tokens.containsKey(AvailableToken.HELP.getName())) {
            return;
        }

        for (AvailableToken t : AvailableToken.values()) {
            if (t.getName().equals("ip") && t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeConnect() {
        String address = getTokens().get(AvailableToken.IP.getName());
        Connection connection = new Connection(address);
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("client\\src\\main\\resources\\configuration.xml");
            properties.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverPort = properties.getProperty("server.port");
        connection.connect(serverPort);
        Controller.getInstance().setConnection(connection);
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   connect -ip='192.168.0.1' [-help]");
    }
}
