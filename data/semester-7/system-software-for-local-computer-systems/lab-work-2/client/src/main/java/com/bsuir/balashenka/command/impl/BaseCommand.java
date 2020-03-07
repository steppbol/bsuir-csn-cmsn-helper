package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand implements Command {
    private Map<String, String> availableTokens;
    private Map<String, String> tokens;
    private String command;

    BaseCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    @Override
    public final void verifyTokens() throws WrongCommandFormatException {

        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> entrySet : tokens.entrySet()) {
                final String key = entrySet.getKey();
                if (!availableTokens.containsKey(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token.");
                }
            }
        }
    }

    @Override
    public final Map<String, String> getAllTokens() {
        return this.tokens;
    }

    @Override
    public final void putToken(String name, String value) {
        this.tokens.put(name, value);
    }

    @Override
    public final void validateTokens() throws WrongCommandFormatException {
        for (Map.Entry<String, String> token : getAllTokens().entrySet()) {
            String tokenKey = token.getKey();
            String tokenValue = token.getValue();

            String regex = availableTokens.get(tokenKey);

            if (!validateToken(tokenValue, regex)) {
                throw new WrongCommandFormatException("Token '" + tokenKey + "' is incorrect.");
            }
        }
    }

    @Override
    public final boolean validateToken(String tokenValue, String regex) {
        return (tokenValue == null && regex == null)
                || (tokenValue != null && !tokenValue.isEmpty() && !regex.isEmpty() && tokenValue.matches(regex));
    }

    @Override
    public Map<String, String> getAvailableTokens() {
        return availableTokens;
    }

    @Override
    public void setAvailableTokens(Map<String, String> availableTokens) {
        this.availableTokens = availableTokens;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }
}
