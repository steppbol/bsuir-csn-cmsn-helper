package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.CommandToken;

import java.util.Map;

public class ExitCommand extends BaseCommand {
    private static final CommandToken COMMAND_FORCE = new CommandToken("force", null, false);
    private static final CommandToken COMMAND_HELP = new CommandToken("help", null, false);

    public ExitCommand() {
        super(COMMAND_FORCE,
                COMMAND_HELP);
    }

    @Override
    public void execute() {
        try {
            validateTokensByRegex();
            checkTokenCount();

            Connection connection = Controller.getInstance().getConnection();
            Map<String, String> tokens = getAllTokens();

            if (tokens.size() > 0) {
                if (tokens.containsKey(COMMAND_HELP.getName())) {
                    executeHelp();
                } else {
                    executeForceExit();
                }
            } else {
                if (connection == null) {
                    Controller.getInstance().getKeyboard().wantExit(true);
                } else {
                    System.out.println("Connection is opened. Please, close connection to terminate program.");
                }
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command buildCommandInstance() {
        return new ExitCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if (getAllTokens().size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }
    }

    private void executeForceExit() {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            connection.close();
        }

        Controller.getInstance().getKeyboard().wantExit(true);
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   exit [-force] [-help]");
    }
}
