package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.command.impl.*;
import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.CommandDescription;
import com.bsuir.balashenka.util.CommandRegister;
import com.bsuir.balashenka.util.InputManager;

public final class Controller {
    private static Controller instance;
    private Connection connection;
    private InputManager keyboard = new InputManager();

    private Controller() {
        CommandRegister.init(
                new CommandDescription("connect", "Connect to server", new ConnectCommand()),
                new CommandDescription("disconnect", "Disconnect from server", new DisconnectCommand()),
                new CommandDescription("download", "Download file from server", new DownloadCommand()),
                new CommandDescription("echo", "Check server echo", new EchoCommand()),
                new CommandDescription("time", "Get server time", new TimeCommand()),
                new CommandDescription("help", "Display help information about available commands", new HelpCommand()),
                new CommandDescription("exit", "Terminate program", new ExitCommand())
        );

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
        do {
            try {
                Command command = keyboard.getCommand();
                command.execute();
            } catch (WrongCommandFormatException | CommandNotFoundException | CannotExecuteCommandException e) {
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
