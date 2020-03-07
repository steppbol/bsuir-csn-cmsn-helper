package com.bsuir.balashenka.controller.impl;

import com.bsuir.balashenka.controller.ServerController;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.service.impl.TcpConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class TcpServerController implements ServerController {
    private static volatile TcpServerController instance;

    private TcpServerController() {
    }

    public static TcpServerController getInstance() {
        TcpServerController localInstance = instance;
        if (localInstance == null) {
            synchronized (TcpConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TcpServerController();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void work() {
        Connection connection = TcpConnection.getInstance();
        Properties properties = new Properties();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("server\\src\\main\\resources\\configuration.xml");
            properties.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serverPort = properties.getProperty("server.port");
        String serverBacklog = properties.getProperty("server.backlog");
        connection.run(serverPort, serverBacklog);
        while (true) {
            try {
                connection.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public TcpConnection getConnection() {
        return TcpConnection.getInstance();
    }
}
