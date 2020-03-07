package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;

public class DisconnectCommand extends BaseCommand {
    @Override
    public void execute() {
        try {
            checkTokenCount();
            Connection connection = Controller.getInstance().getConnection();
            if (connection != null) {
                connection.close();
                System.out.println("You've been disconnected from server");
            } else {
                System.out.println("You're not connected to server");
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new DisconnectCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if (getTokens().size() > 0) {
            throw new WrongCommandFormatException("Command hasn't any tokens. See -help");
        }
    }
}
