package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
abstract class BaseCommand implements Command {
    private Map<String, String> availableTokens;
    private Map<String, String> tokens;

    public BaseCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    @Override
    public final void verifyTokens() throws WrongCommandFormatException {
        log.debug("Tokens: " + tokens);

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
    public final void putToken(String name, String value) {
        this.tokens.put(name, value);
    }

    public final Map<String, String> getTokens() {
        return this.tokens;
    }

    public Map<String, String> getAvailableTokens() {
        return availableTokens;
    }
}
