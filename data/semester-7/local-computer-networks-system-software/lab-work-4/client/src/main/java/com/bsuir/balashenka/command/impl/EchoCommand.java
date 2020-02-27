package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.AvailableTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableToken;

import java.util.Arrays;
import java.util.Map;

public class EchoCommand extends BaseCommand {

    public EchoCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            Map<String, String> toks = getTokens();

            String firstKey = String.valueOf(toks.keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            switch (currentToken) {
                case CONTENT:
                    executeEcho();
                    break;
                case HELP:
                    executeHelp();
                    break;
            }
        } catch (WrongCommandFormatException | AvailableTokenNotPresentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new EchoCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getTokens();

        if (tokens.size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }

        if (tokens.containsKey(AvailableToken.HELP.getName())) {
            return;
        }

        for (AvailableToken t : AvailableToken.values()) {
            if (t.getName().equals("content") && t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeEcho() {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            if (connection.sendMessage(getCommand())) {
                connection.receive();
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
