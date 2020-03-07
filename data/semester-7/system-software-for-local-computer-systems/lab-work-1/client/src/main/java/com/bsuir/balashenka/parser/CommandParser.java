package com.bsuir.balashenka.parser;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

public interface CommandParser {
    Command handle(String cmd) throws WrongCommandFormatException, CommandNotFoundException;

    default Command parse(String cmd) throws WrongCommandFormatException, CommandNotFoundException {
        return handle(cmd);
    }
}
