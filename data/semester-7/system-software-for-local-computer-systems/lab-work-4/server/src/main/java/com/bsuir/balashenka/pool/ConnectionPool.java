package com.bsuir.balashenka.pool;

import com.bsuir.balashenka.service.DefaultClientConnection;
import com.bsuir.balashenka.service.DefaultServerConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public final class ConnectionPool {
    private static ConnectionPool instance;
    private static boolean createdInstance = false;
    private int poolSize;
    private int lastConnectionIndex;
    private int availableConnectionsCount;
    private Map<DefaultClientConnection, Thread> connections;
    private DefaultServerConnection serverConnection;

    private ConnectionPool() {
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
        String serverPoolSize = properties.getProperty("server.pool-size");

        connections = new HashMap<>();
        serverConnection = new DefaultServerConnection();
        serverConnection.open(serverPort, serverBacklog);
        ReentrantLock lock = new ReentrantLock();
        poolSize = Integer.parseInt(serverPoolSize);
        log.info("Maximum pool size: {}", poolSize);
        for (int i = 0; i < poolSize; i++) {
            DefaultClientConnection connection = new DefaultClientConnection(i + 1, lock);
            connections.put(connection, new Thread(connection));
        }
    }

    public static ConnectionPool getInstance() {
        if (!createdInstance) {
            if (instance == null) {
                instance = new ConnectionPool();
                createdInstance = true;
                log.debug(ConnectionPool.class + " instance created!");
            }
        }
        return instance;
    }

    public DefaultServerConnection getServerConnection() {
        return serverConnection;
    }

    public void incAvailableConnections() {
        ++availableConnectionsCount;
    }

    public void decAvailableConnections() {
        --availableConnectionsCount;
    }

    public void incLastConnectionIndex() {
        ++lastConnectionIndex;
    }

    public void decLastConnectionIndex() {
        --lastConnectionIndex;
    }

    public boolean hasAvailableConnection() {
        if (availableConnectionsCount <= 0) {
            log.info("No available connection");
        } else {
            log.info("Available connection: {}", availableConnectionsCount);
        }
        return availableConnectionsCount > 0;
    }

    public void runListeners() {
        connections.forEach((c, t) -> t.start());
    }

    public void addFreeConnection(DefaultClientConnection connection) {
        Thread connectionThread = new Thread(connection);
        connections.put(connection, connectionThread);
        connectionThread.start();
        log.info("Added new client connection");
    }

    public void removeConnection(DefaultClientConnection connection) {
        connections.remove(connection);
        log.info("Client connection was remove");
    }

    public int getActualPoolSize() {
        return connections.size();
    }

    public int getPoolSize() {
        return poolSize;
    }

    public int getLastConnectionIndex() {
        return lastConnectionIndex;
    }
}
