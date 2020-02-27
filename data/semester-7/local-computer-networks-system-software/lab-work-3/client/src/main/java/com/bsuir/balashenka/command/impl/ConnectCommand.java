package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.service.impl.TcpConnection;
import com.bsuir.balashenka.service.impl.UdpConnection;
import com.bsuir.balashenka.util.CommandToken;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConnectCommand extends BaseCommand {
    private static final String SERVER_IP_REGEX = "^(\\d{1,3}\\.){3}\\d{1,3}$";
    private static final String PROTOCOL_TCP = "tcp";
    private static final String PROTOCOL_UDP = "udp";
    private static final CommandToken COMMAND_IP = new CommandToken("ip", SERVER_IP_REGEX, true);
    private static final CommandToken COMMAND_PROTOCOL = new CommandToken("p", "(" + PROTOCOL_TCP + "|" + PROTOCOL_UDP + ")", true);
    private static final CommandToken COMMAND_HELP = new CommandToken("help", null, false);

    public ConnectCommand() {
        super(COMMAND_HELP,
                COMMAND_IP,
                COMMAND_PROTOCOL);
    }

    @Override
    public void execute() {
        try {
            validateRequiredTokens();
            validateTokensByRegex();
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
                System.out.println("Wrong protocol.");
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command buildCommandInstance() {
        return new ConnectCommand();
    }

    private void executeConnectTcp() {
        String address = getAllTokens().get(COMMAND_IP.getName());
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("client\\src\\main\\resources\\configuration.xml");
            properties.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverPort = properties.getProperty("server.port");
        Connection connection = new TcpConnection(serverPort);

        if (connection.connect(address)) {
            Controller.getInstance().setConnection(connection);
            System.out.println("Connected to TCP server: " + connection.getRemoteSocketIpAddress());
        } else {
            System.out.println("Can't connect to TCP server.");
        }
    }

    private void executeConnectUdp() {
        String address = getAllTokens().get(COMMAND_IP.getName());
        Connection connection = new UdpConnection();

        if (connection.connect(address)) {
            Controller.getInstance().setConnection(connection);
            System.out.println("Connected to UDP server: " + connection.getRemoteSocketIpAddress());
        } else {
            System.out.println("Can't connect to UDP server.");
        }
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   connect -ip='192.168.0.1' -p='tcp/udp' [-help]");
    }
}
