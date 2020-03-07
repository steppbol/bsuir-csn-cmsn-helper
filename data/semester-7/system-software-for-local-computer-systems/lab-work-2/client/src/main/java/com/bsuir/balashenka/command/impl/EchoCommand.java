package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.controller.impl.ClientController;
import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.exception.CommandTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableCommandToken;
import com.bsuir.balashenka.util.CommandToken;

import java.net.SocketTimeoutException;
import java.util.Map;

public class EchoCommand extends BaseCommand {
    private final CommandToken COMMAND_CONTENT = new CommandToken("content", "^[\\w .-]+$", true);
    private final CommandToken COMMAND_HELP = new CommandToken("help", null, false);
    private Controller clientController;
    private AvailableCommandToken availableCommandTokens = new AvailableCommandToken();

    public EchoCommand() {
        availableCommandTokens.addToken(COMMAND_CONTENT);
        availableCommandTokens.addToken(COMMAND_HELP);
        clientController = ClientController.getInstance();
        availableCommandTokens.getTokens().forEach((t) -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            Map<String, String> tokens = getAllTokens();

            String firstKey = String.valueOf(tokens.keySet().toArray()[0]);
            CommandToken currentToken = availableCommandTokens.find(firstKey);

            if (currentToken.equals(COMMAND_CONTENT)) {
                executeEcho();
            } else if (currentToken.equals(COMMAND_HELP)) {
                executeHelp();
            }
        } catch (WrongCommandFormatException | CommandTokenNotPresentException | CannotExecuteCommandException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new EchoCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getAllTokens();

        if (tokens.size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }

        if (tokens.containsKey(COMMAND_HELP.getName())) {
            return;
        }

        for (CommandToken t : availableCommandTokens.getTokens()) {
            if (t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeEcho() throws CannotExecuteCommandException {
        Connection connection = clientController.getConnection();

        if (connection != null) {
            connection.send(getCommand());
            String ans;
            try {
                ans = connection.receive();
            } catch (SocketTimeoutException e) {
                throw new CannotExecuteCommandException(e);
            }
            System.out.println(ans);
        } else {
            System.out.println("You're not connected to server.");
        }
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   echo -content='Your some text' [-help]");
    }
}
