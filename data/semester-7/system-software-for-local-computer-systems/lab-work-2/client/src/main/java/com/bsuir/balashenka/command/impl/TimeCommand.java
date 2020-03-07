package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.controller.impl.ClientController;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;

import java.net.SocketTimeoutException;

public class TimeCommand extends BaseCommand {
    private Controller clientController;

    public TimeCommand() {
        clientController = ClientController.getInstance();
    }

    @Override
    public void execute() {
        try {
            checkTokenCount();
            Connection connection = clientController.getConnection();

            if (connection != null) {
                executeGettingTime(connection);
            } else {
                System.out.println("You're not connected to server.");
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new TimeCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if (getAllTokens().size() > 0) {
            throw new WrongCommandFormatException("Command hasn't any tokens.");
        }
    }

    private void executeGettingTime(Connection connection) {
        connection.send(getCommand());
        String time = null;
        try {
            time = connection.receive();
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout error. Check connection.");
        }
        System.out.println("Server time: " + time);
    }
}

