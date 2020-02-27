package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.util.AvailableCommandTokens;
import com.bsuir.balashenka.util.CommandToken;

import java.util.HashMap;
import java.util.Map;

abstract class BaseCommand implements Command {
    private String command;
    private AvailableCommandTokens availableCommandTokens = new AvailableCommandTokens();
    private Map<String, String> tokens;
    private boolean done = false;

    public BaseCommand(CommandToken... commandTokens) {
        for (CommandToken commandToken : commandTokens) {
            availableCommandTokens.addToken(commandToken);
        }
        tokens = new HashMap<>();
    }

    @Override
    public final void verifyTokens() throws WrongCommandFormatException {

        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> fl : tokens.entrySet()) {
                String key = fl.getKey();

                if (!availableCommandTokens.hasToken(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token.");
                }
            }
        }
    }

    public final Map<String, String> getAllTokens() {
        return this.tokens;
    }

    @Override
    public final void putToken(String name, String value) {
        this.tokens.put(name, value);
    }

    @Override
    public final void validateRequiredTokens() throws WrongCommandFormatException {
        Map<String, String> tokens = getAllTokens();
        int numberOfRequiredTokens = (int) availableCommandTokens.getTokens().stream().filter(CommandToken::isRequired).count();
        if (tokens.size() > numberOfRequiredTokens) {
            throw new WrongCommandFormatException("This command should have only " + numberOfRequiredTokens + " tokens.");
        }
        if (tokens.containsKey("help")) {
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

    @Override
    public final void validateTokensByRegex() throws WrongCommandFormatException {
        for (Map.Entry<String, String> token : getAllTokens().entrySet()) {
            String tokenKey = token.getKey();
            String tokenValue = token.getValue();
            String regex = null;
            try {
                regex = availableCommandTokens.find(tokenKey).getRegex();
            } catch (CommandTokenNotPresentException e) {
                e.printStackTrace();
            }

            if (!validateTokenByRegex(tokenValue, regex)) {
                throw new WrongCommandFormatException("Token '" + tokenKey + "' is incorrect.");
            }
        }
    }

    @Override
    public final boolean validateTokenByRegex(String tokenValue, String regex) {
        return (tokenValue == null && regex == null)
                || (tokenValue != null && !tokenValue.isEmpty() && !regex.isEmpty() && tokenValue.matches(regex));
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean isDone) {
        done = isDone;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
