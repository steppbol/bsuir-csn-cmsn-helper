package com.bsuir.balashenka.parser.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.util.CommandType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenParser {
    private static final String CMD_TOKEN_REGEX = "(-([a-z_]+)((?==)='([\\w .-:\\\\]+)')*)";
    private static final int TOKEN_NAME_GROUP_INDEX = 2;
    private static final int TOKEN_VALUE_GROUP_INDEX = 4;
    private Command command;

    public TokenParser(String commandName) throws CommandNotFoundException {
        this.command = CommandType.findCommand(commandName);
    }

    public Command handle(String currentCommand) throws WrongCommandFormatException {
        Pattern pattern = Pattern.compile(CMD_TOKEN_REGEX);
        Matcher matcher = pattern.matcher(currentCommand);

        while (matcher.find()) {
            final String tokenName = matcher.group(TOKEN_NAME_GROUP_INDEX);
            final String tokenValue = matcher.group(TOKEN_VALUE_GROUP_INDEX);
            command.putToken(tokenName, tokenValue);
        }
        command.verifyTokens();
        return command;
    }
}
