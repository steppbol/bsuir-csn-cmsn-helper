package com.bsuir.balashenka.parser.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.util.CommandRegister;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser extends DefaultParser {
    private static final String CMD_COMMON_REGEX = "^([a-z]+)( -[a-z]+((?==)='[\\w .-:\\\\]+')*)*$";
    private static final int COMMAND_GROUP_INDEX = 1;
    private CommandRegister commandType;

    public CommandParser(CommandRegister commandType) {
        this.commandType = commandType;
    }

    @Override
    public Command handle(String cmd) throws WrongCommandFormatException, CommandNotFoundException {
        Pattern pattern = Pattern.compile(CMD_COMMON_REGEX);
        Matcher matcher = pattern.matcher(cmd);

        if (!matcher.find()) {
            throw new WrongCommandFormatException("Wrong command format.");
        }

        String commandName = matcher.group(COMMAND_GROUP_INDEX);

        if (commandType.hasCommand(commandName)) {
            return new TokenParser(commandType, commandName).handle(cmd);
        }

        throw new CommandNotFoundException("Wrong command: " + commandName);
    }
}
