package com.bsuir.balashenka.command;

import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.DefaultServerConnection;

import java.util.Map;

public interface Command {
    void execute(DefaultServerConnection connection);

    void putToken(String name, String value);

    Map<String, String> getTokens();

    void verifyTokens() throws WrongCommandFormatException;

    Command build();
}
