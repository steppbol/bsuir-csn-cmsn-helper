package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.CommandToken;

import java.net.SocketTimeoutException;
import java.util.Map;

public class EchoCommand extends BaseCommand {
    private static final CommandToken COMMAND_CONTENT = new CommandToken("content", "^[\\w .-]+$", true);
    private static final CommandToken COMMAND_HELP = new CommandToken("help", null, false);

    public EchoCommand() {
        super(COMMAND_CONTENT,
                COMMAND_HELP);
    }

    @Override
    public void execute() throws CannotExecuteCommandException {
        try {
            validateRequiredTokens();
            validateTokensByRegex();

            Map<String, String> tokens = getAllTokens();

            if (tokens.containsKey(COMMAND_HELP.getName())) {
                executeHelp();
            } else {
                executeEcho();
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command buildCommandInstance() {
        return new EchoCommand();
    }

    private void executeEcho() throws CannotExecuteCommandException {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            if (connection.sendMessage(getCommand())) {
                String ans;
                try {
                    ans = connection.read();
                } catch (SocketTimeoutException e) {
                    throw new CannotExecuteCommandException(e);
                }
            }
        } else {
            System.out.println("You're not connected to server.");
        }
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   echo -content='Your some text' [-help]");
    }
}
