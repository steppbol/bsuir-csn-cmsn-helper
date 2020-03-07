package com.bsuir.balashenka.command;

import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

import java.util.Map;

public interface Command {

    void execute() throws CannotExecuteCommandException;

    void putToken(String name, String value);

    Map<String, String> getAllTokens();

    void verifyTokens() throws WrongCommandFormatException;

    Command buildCommandInstance();

    void validateRequiredTokens() throws WrongCommandFormatException;

    void validateTokensByRegex() throws WrongCommandFormatException;

    boolean validateTokenByRegex(String tokenValue, String regex);

    void setCommand(String command);

    boolean isDone();
}
