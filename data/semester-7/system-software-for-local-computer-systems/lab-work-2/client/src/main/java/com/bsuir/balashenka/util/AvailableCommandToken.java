package com.bsuir.balashenka.util;


import com.bsuir.balashenka.exception.CommandTokenNotPresentException;

import java.util.ArrayList;

public class AvailableCommandToken {

    private ArrayList<CommandToken> tokens = new ArrayList<>();

    public void addToken(CommandToken token) {
        tokens.add(token);
    }

    public ArrayList<CommandToken> getTokens() {
        return tokens;
    }

    public CommandToken find(String name) throws CommandTokenNotPresentException {
        for (CommandToken t : tokens) {
            if (t.getName().equals(name)) {
                return t;
            }
        }

        throw new CommandTokenNotPresentException("Token '" + name + "' is not available.");
    }
}
