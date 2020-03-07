package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.controller.impl.ClientController;
import com.bsuir.balashenka.exception.CommandTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableCommandToken;
import com.bsuir.balashenka.util.CommandToken;

import java.util.Map;

public class ExitCommand extends BaseCommand {
    private final CommandToken COMMAND_FORCE = new CommandToken("force", null, false);
    private final CommandToken COMMAND_HELP = new CommandToken("help", null, false);
    private Controller clientController;
    private AvailableCommandToken availableCommandTokens = new AvailableCommandToken();

    public ExitCommand() {
        availableCommandTokens.addToken(COMMAND_FORCE);
        availableCommandTokens.addToken(COMMAND_HELP);
        clientController = ClientController.getInstance();
        availableCommandTokens.getTokens().forEach((t) -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute() {
        try {
            validateTokens();
            checkTokenCount();

            Connection connection = clientController.getConnection();
            Map<String, String> tokens = getAllTokens();

            if (tokens.size() > 0) {
                String firstKey = String.valueOf(getAllTokens().keySet().toArray()[0]);
                CommandToken currentToken = availableCommandTokens.find(firstKey);

                if (currentToken.equals(COMMAND_FORCE)) {
                    executeForceExit();
                } else if (currentToken.equals(COMMAND_HELP)) {
                    executeHelp();
                }
            } else {
                if (connection == null) {
                    ClientController.getInstance().getKeyboard().wantExit(true);
                } else {
                    System.out.println("Client connection is opened. Please, close connection to terminate program.");
                }
            }
        } catch (WrongCommandFormatException | CommandTokenNotPresentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new ExitCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if (getAllTokens().size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }
    }

    private void executeForceExit() {
        Connection connection = ClientController.getInstance().getConnection();

        if (connection != null) {
            connection.close();
        }

        ClientController.getInstance().getKeyboard().wantExit(true);
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   exit [-force] [-help]");
    }
}
