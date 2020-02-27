package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseCommand implements Command {
    private Map<String, String> availableTokens;
    private Map<String, String> tokens;

    BaseCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    @Override
    public void verifyTokens() throws WrongCommandFormatException {
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
    public Map<String, String> getAvailableTokens() {
        return this.availableTokens;
    }

    @Override
    public Map<String, String> getAllTokens() {
        return this.tokens;
    }

    @Override
    public void putToken(String name, String value) {
        this.tokens.put(name, value);
    }
}
