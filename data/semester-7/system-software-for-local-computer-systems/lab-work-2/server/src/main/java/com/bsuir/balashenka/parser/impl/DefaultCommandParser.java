package com.bsuir.balashenka.parser.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.parser.CommandParser;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultCommandParser implements CommandParser {
    private static final String CMD_COMMON_REGEX = "^([a-z]+)( -[a-z]+((?==)='[\\w .-:\\\\]+')*)*$";
    private static final int COMMAND_GROUP_INDEX = 1;
    private Class commandType;

    public DefaultCommandParser(Class commandType) {
        this.commandType = commandType;
    }

    @Override
    public Command handle(String cmd) throws WrongCommandFormatException, CommandNotFoundException {
        Pattern pattern = Pattern.compile(CMD_COMMON_REGEX);
        Matcher matcher = pattern.matcher(cmd);

        if (!matcher.find()) {
            throw new WrongCommandFormatException("Wrong command format.");
        }

        final String commandName = matcher.group(COMMAND_GROUP_INDEX);

        try {
            if ((Boolean) commandType.getMethod("hasCommand", String.class).invoke(null, commandName)) {
                return new TokenParser(commandType, commandName).handle(cmd);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        throw new CommandNotFoundException("Wrong command: " + commandName);
    }
}
