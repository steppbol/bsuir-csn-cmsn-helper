package com.bsuir.balashenka.service;

import com.bsuir.balashenka.pool.ConnectionPool;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class DefaultClientConnection implements Runnable {
    private static final int TIMEOUT_MS = 2_000;
    private int index;
    private boolean isRepeated = false;
    private ReentrantLock lock;
    private Socket clientSocket;

    public DefaultClientConnection(final int index, ReentrantLock lock) {
        this.index = index;
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true) {
            ConnectionPool connectionPool = ConnectionPool.getInstance();
            try {
                lock.lock();
                log.info("Thread {} has been locked", index);
                this.acceptClient(connectionPool);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            workWithClient(connectionPool);

            try {
                while (true) {
                    if (connectionPool.getActualPoolSize() <= connectionPool.getPoolSize()) {
                        break;
                    }
                    log.info("Waiting timeout: {} ms to get lock", TIMEOUT_MS);
                    if (!lock.tryLock(TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                        throw new InterruptedException("Lock timeout exceed...");
                    }
                    log.info("Lock access");
                    isRepeated = true;
                    this.acceptClient(connectionPool);
                    lock.unlock();

                    workWithClient(connectionPool);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                connectionPool.decAvailableConnections();
                connectionPool.removeConnection(this);
                connectionPool.hasAvailableConnection();
                log.info("Actual pool size: " + connectionPool.getActualPoolSize());
                break;
            }
        }
    }

    private void acceptClient(ConnectionPool connectionPool) throws InterruptedException {
        clientSocket = connectionPool.getServerConnection().accept();
        connectionPool.decAvailableConnections();
        log.info("Thread " + index + " has been accepted.");
        if (!connectionPool.hasAvailableConnection() && !isRepeated) {
            connectionPool.incLastConnectionIndex();
            int index = connectionPool.getLastConnectionIndex();
            DefaultClientConnection connection = new DefaultClientConnection(index, lock);
            connectionPool.addFreeConnection(connection);
            connectionPool.incAvailableConnections();
            log.info("Added new thread to pool. Actual pool size: {}", connectionPool.getActualPoolSize());
            connectionPool.hasAvailableConnection();
        }
    }

    private void workWithClient(ConnectionPool connectionPool) {
        DefaultServerConnection connection = new DefaultServerConnection();
        connection.listen(clientSocket, index);
        connectionPool.incLastConnectionIndex();
        index = connectionPool.getLastConnectionIndex();
        connectionPool.incAvailableConnections();
        connectionPool.hasAvailableConnection();
    }
}
