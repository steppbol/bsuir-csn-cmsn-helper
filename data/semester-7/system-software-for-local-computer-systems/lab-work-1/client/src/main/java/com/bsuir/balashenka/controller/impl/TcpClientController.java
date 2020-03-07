package com.bsuir.balashenka.controller.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ClientController;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.service.impl.TcpConnection;
import com.bsuir.balashenka.util.InputManager;

public class TcpClientController implements ClientController {
    private static TcpClientController instance;
    private Connection connection;
    private InputManager keyboard;

    private TcpClientController() {
        keyboard = new InputManager();
    }

    public static TcpClientController getInstance() {
        TcpClientController localInstance = instance;
        if (localInstance == null) {
            synchronized (TcpConnection.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TcpClientController();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void work() {
        do {
            try {
                Command command = keyboard.getCommand();
                command.execute();
            } catch (WrongCommandFormatException | CommandNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } while (!keyboard.enteredExit());
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public InputManager getKeyboard() {
        return keyboard;
    }
}
