package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ClientController;
import com.bsuir.balashenka.controller.impl.TcpClientController;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;

public class TimeCommand extends BaseCommand {
    private ClientController clientController;

    public TimeCommand() {
        this.clientController = TcpClientController.getInstance();
    }

    @Override
    public void execute() {
        try {
            checkTokenCount();
            Connection tcpConnection = clientController.getConnection();

            if (tcpConnection != null) {
                executeGettingTime(tcpConnection);
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
        if (getTokens().size() > 0) {
            throw new WrongCommandFormatException("Command hasn't any tokens.");
        }
    }

    private void executeGettingTime(Connection connection) {
        connection.send(getCommand());
        System.out.println("Server time: " + connection.receive());
    }
}

