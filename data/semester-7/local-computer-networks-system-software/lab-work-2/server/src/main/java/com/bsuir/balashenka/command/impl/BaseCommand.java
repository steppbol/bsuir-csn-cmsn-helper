package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand implements Command {
    private Map<String, String> availableTokens;
    private Map<String, String> tokens;

    public BaseCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    @Override
    public void verifyTokens() throws WrongCommandFormatException {
        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> tokensSet : tokens.entrySet()) {
                final String key = tokensSet.getKey();
                if (!availableTokens.containsKey(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token");
                }
            }
        }
    }

    @Override
    public void putTokenToAllTokens(String name, String value) {
        tokens.put(name, value);
    }

    @Override
    public boolean validateToken(String tokenValue, String regex) {
        return (tokenValue == null && regex == null)
                || (tokenValue != null && !tokenValue.isEmpty() && !regex.isEmpty() && tokenValue.matches(regex));
    }

    public Map<String, String> getAllTokens() {
        return tokens;
    }

    public Map<String, String> getAvailableTokens() {
        return availableTokens;
    }
}
