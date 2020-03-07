package com.bsuir.balashenka.command;

import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.Map;

public interface Command {
    void execute();

    void putTokenToAllTokens(String name, String value);

    Map<String, String> getAllTokens();

    void verifyTokens() throws WrongCommandFormatException;

    Command build();

    boolean validateToken(String tokenValue, String regex);
}
