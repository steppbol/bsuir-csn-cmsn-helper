package com.bsuir.balashenka.parser.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.util.CommandRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenParser extends DefaultParser {
    private static final String CMD_TOKEN_REGEX = "(-([a-z]+)((?==)='([\\w .-:\\\\]+)')*)";
    private static final int TOKEN_NAME_GROUP_INDEX = 2;
    private static final int TOKEN_VALUE_GROUP_INDEX = 4;
    private Command command;

    public TokenParser(CommandRegister commandType, String commandName) throws CommandNotFoundException {
        this.command = commandType.findCommand(commandName);
    }

    @Override
    public Command handle(String cmd) throws WrongCommandFormatException {
        command.setCommand(cmd);

        Pattern pattern = Pattern.compile(CMD_TOKEN_REGEX);
        Matcher matcher = pattern.matcher(cmd);

        while (matcher.find()) {
            String tokenName = matcher.group(TOKEN_NAME_GROUP_INDEX);
            String tokenValue = matcher.group(TOKEN_VALUE_GROUP_INDEX);

            command.putToken(tokenName, tokenValue);
        }

        command.verifyTokens();
        return command;
    }
}