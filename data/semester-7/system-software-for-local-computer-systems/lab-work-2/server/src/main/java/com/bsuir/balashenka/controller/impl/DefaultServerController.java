package com.bsuir.balashenka.controller.impl;

import com.bsuir.balashenka.controller.ServerController;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.service.impl.TcpConnection;
import com.bsuir.balashenka.service.impl.UdpConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class DefaultServerController implements ServerController {
    private static final String PROTOCOL_TCP = "tcp";
    private static final String PROTOCOL_UDP = "udp";
    private static volatile DefaultServerController instance;
    private Connection connection;

    private DefaultServerController() {
    }

    public static DefaultServerController getInstance() {
        DefaultServerController localInstance = instance;
        if (localInstance == null) {
            synchronized (TcpConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DefaultServerController();
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
        String serverType = properties.getProperty("server.type");
        String serverPort = properties.getProperty("server.port");
        String serverBacklog = properties.getProperty("server.backlog");
        if (PROTOCOL_TCP.equals(serverType)) {
            connection = TcpConnection.getInstance();
            log.info("TCP Server has been started");
        } else if (PROTOCOL_UDP.equals(serverType)) {
            log.info("UDP Server has been started");
            connection = UdpConnection.getInstance();
        }
        try {
            connection.run(serverPort, serverBacklog);
            connection.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
