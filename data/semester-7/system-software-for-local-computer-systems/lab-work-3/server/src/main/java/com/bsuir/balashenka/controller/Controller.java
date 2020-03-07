package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.command.impl.*;
import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.parser.impl.CommandParser;
import com.bsuir.balashenka.service.ClientConnection;
import com.bsuir.balashenka.service.impl.tcp.TcpServerConnection;
import com.bsuir.balashenka.service.impl.udp.UdpServerConnection;
import com.bsuir.balashenka.util.CommandDescription;
import com.bsuir.balashenka.util.CommandRegister;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

@Slf4j
public final class Controller {
    private static Controller instance;
    private TcpServerConnection tcpServerConnection;
    private UdpServerConnection udpServerConnection;
    private HashMap<ClientConnection, ServerCommand> connections;

    private Controller() {
        CommandRegister.init(
                new CommandDescription("echo", "Echo server", new EchoCommand()),
                new CommandDescription("time", "Get server time", new TimeCommand()),
                new CommandDescription("download", "Download file from server", new DownloadCommand())
        );
        connections = new HashMap<>();
    }

    public static Controller getInstance() {
        Controller localInstance = instance;
        if (localInstance == null) {
            synchronized (Controller.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Controller();
                }
            }
        }
        return localInstance;
    }

    public void work() {
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("server\\src\\main\\resources\\configuration.xml");
            properties.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String tcpServerPort = properties.getProperty("tcp.server.port");
        String udpServerPort = properties.getProperty("udp.server.port");
        String tcpServerBacklog = properties.getProperty("tcp.server.backlog");

        try {
            log.info("TCP Server is starting...");
            tcpServerConnection = new TcpServerConnection(tcpServerPort, tcpServerBacklog);
            log.info("TCP Server is started at {}", tcpServerConnection.getAddress());
            log.info("UDP Server is starting...");
            udpServerConnection = new UdpServerConnection(udpServerPort);
            log.info("UDP Server is started at {}", udpServerConnection.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        log.info("All Servers are started");
        while (true) {
            ClientConnection newConnection = getClient(5);
            if (newConnection != null) {
                connections.put(newConnection, null);
                log.info("Connected new client. Count of connections: {}", connections.size());
            }

            connections.forEach((connection, currentCommand) -> {
                if (currentCommand == null) {
                    try {
                        String message = connection.read(5);
                        log.info("Client {} message: {}", connection.getAddress(), message);
                        if (message != null) {
                            ServerCommand command = (ServerCommand) new CommandParser(CommandRegister.getInstance()).handle(message);
                            connections.put(connection, command);
                        }
                    } catch (SocketException e) {
                        log.info("Connection from {} lost", connection.getAddress());
                    } catch (IOException | WrongCommandFormatException | CommandNotFoundException e) {
                        e.printStackTrace();
                    } catch (TimeoutException ignored) {
                    }
                } else {
                    try {
                        currentCommand.execute(connection);
                        if (currentCommand.isDone()) {
                            connections.put(connection, null);
                        }
                    } catch (CannotExecuteCommandException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private ClientConnection getClient(int timeout) {
        try {
            return tcpServerConnection.open(timeout);
        } catch (TimeoutException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return udpServerConnection.open(timeout);
        } catch (TimeoutException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}