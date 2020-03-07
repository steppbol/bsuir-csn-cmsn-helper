package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.pool.ConnectionPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerController {
    private static volatile ServerController instance;

    private ServerController() {
    }

    public static ServerController getInstance() {
        ServerController localInstance = instance;
        if (localInstance == null) {
            synchronized (ServerController.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new ServerController();
                    log.debug(ServerController.class.getName() + " instance created!");
                }
            }
        }
        return localInstance;
    }

    public void work() {
        ConnectionPool.getInstance().runListeners();
    }
}
