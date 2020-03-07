package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.InputManager;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public final class Controller {
    private static Controller instance;
    private static AtomicBoolean createdInstance = new AtomicBoolean(false);
    private static ReentrantLock lock = new ReentrantLock();
    private Connection connection;
    private InputManager keyboard;

    private Controller() {
        keyboard = new InputManager();
    }

    public static Controller getInstance() {
        if (!createdInstance.get()) {
            try {
                lock.lock();

                if (instance == null) {
                    instance = new Controller();
                    createdInstance.set(true);
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    public void work() {
        do {
            try {
                Command command = keyboard.getCommand();
                command.execute();
            } catch (WrongCommandFormatException | CommandNotFoundException e) {
                e.printStackTrace();
            }
        } while (!keyboard.enteredExit());
        System.out.println("Program is terminated.");
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection c) {
        this.connection = c;
    }

    public InputManager getKeyboard() {
        return keyboard;
    }
}
