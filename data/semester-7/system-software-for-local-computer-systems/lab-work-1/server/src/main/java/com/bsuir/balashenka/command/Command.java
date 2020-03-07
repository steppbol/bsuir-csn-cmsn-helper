package com.bsuir.balashenka.command;

import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.Map;

public interface Command {
    void execute();

    void putToken(String name, String value);

    Map<String, String> getAllTokens();

    Map<String, String> getAvailableTokens();

    void verifyTokens() throws WrongCommandFormatException;

    Command build();
}
