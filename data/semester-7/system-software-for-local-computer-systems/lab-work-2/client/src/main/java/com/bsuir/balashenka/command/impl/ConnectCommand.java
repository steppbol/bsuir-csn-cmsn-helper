package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.impl.ClientController;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.impl.TcpConnection;
import com.bsuir.balashenka.service.impl.UdpConnection;
import com.bsuir.balashenka.util.AvailableCommandToken;
import com.bsuir.balashenka.util.CommandToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class ConnectCommand extends BaseCommand {
    private static final String PROTOCOL_TCP = "tcp";
    private static final String PROTOCOL_UDP = "udp";
    private final CommandToken COMMAND_IP = new CommandToken("ip", "^(\\d{1,3}\\.){3}\\d{1,3}$", true);
    private final CommandToken COMMAND_PROTOCOL = new CommandToken("p", "(" + PROTOCOL_TCP + "|" + PROTOCOL_UDP + ")", true);
    private final CommandToken COMMAND_HELP = new CommandToken("help", null, false);
    private AvailableCommandToken availableCommandTokens = new AvailableCommandToken();

    public ConnectCommand() {
        availableCommandTokens.addToken(COMMAND_HELP);
        availableCommandTokens.addToken(COMMAND_PROTOCOL);
        availableCommandTokens.addToken(COMMAND_IP);

        getAvailableTokens().put(COMMAND_HELP.getName(), COMMAND_HELP.getRegex());
        getAvailableTokens().put(COMMAND_IP.getName(), COMMAND_IP.getRegex());
        getAvailableTokens().put(COMMAND_PROTOCOL.getName(), COMMAND_PROTOCOL.getRegex());
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            if (getAllTokens().containsKey(COMMAND_HELP.getName())) {
                executeHelp();
                return;
            }
            String protocol = getAllTokens().get(COMMAND_PROTOCOL.getName());
            if (PROTOCOL_TCP.equals(protocol)) {
                executeConnectTcp();
            } else if (PROTOCOL_UDP.equals(protocol)) {
                executeConnectUdp();
            } else {
                System.out.println("Wrong protocol");
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new ConnectCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getAllTokens();

        if (tokens.size() > 2) {
            throw new WrongCommandFormatException("This command should have only two tokens.");
        }

        if (tokens.containsKey(COMMAND_HELP.getName())) {
            return;
        }

        for (CommandToken t : availableCommandTokens.getTokens()) {
            if (t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeConnectTcp() {
        String address = getAllTokens().get(COMMAND_IP.getName());
        TcpConnection connection = TcpConnection.getInstance();
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("client\\src\\main\\resources\\configuration.xml");
            properties.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverPort = properties.getProperty("server.port");
        connection.connect(address, serverPort);
        ClientController.getInstance().setConnection(connection);
        System.out.println("Connected to server: " + connection.getRemoteSocketIpAddress());
    }

    private void executeConnectUdp() {
        String address = getAllTokens().get(COMMAND_IP.getName());
        UdpConnection connection = UdpConnection.getInstance();
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("client\\src\\main\\resources\\configuration.xml");
            properties.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverPort = properties.getProperty("server.port");
        connection.connect(address, serverPort);
        ClientController.getInstance().setConnection(connection);
        System.out.println("Connected to server.");
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   connect -ip='192.168.0.1' -p='tcp/udp' [-help]");
    }
}
