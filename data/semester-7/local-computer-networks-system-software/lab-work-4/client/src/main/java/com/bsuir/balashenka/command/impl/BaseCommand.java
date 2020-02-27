package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.HashMap;
import java.util.Map;

abstract class BaseCommand implements Command {
    private Map<String, String> availableTokens;
    private Map<String, String> tokens;
    private String command;

    public BaseCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    @Override
    public void verifyTokens() throws WrongCommandFormatException {
        System.out.println("Tokens: " + tokens);

        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> fl : tokens.entrySet()) {
                final String key = fl.getKey();

                if (!availableTokens.containsKey(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token.");
                }
            }
        }
    }

    @Override
    public void putToken(String name, String value) {
        this.tokens.put(name, value);
    }

    @Override
    public void validateTokens() throws WrongCommandFormatException {
        for (Map.Entry<String, String> token : getTokens().entrySet()) {
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

    public Map<String, String> getAvailableTokens() {
        return availableTokens;
    }

    public void setAvailableTokens(Map<String, String> availableTokens) {
        this.availableTokens = availableTokens;
    }

    public Map<String, String> getTokens() {
        return this.tokens;
    }

    public void setTokens(Map<String, String> tokens) {
        this.tokens = tokens;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
