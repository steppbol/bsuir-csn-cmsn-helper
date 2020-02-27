package com.bsuir.balashenka.parser.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.parser.Parser;

abstract class BaseParser implements Parser {
    public abstract Command handle(String cmd) throws WrongCommandFormatException, CommandNotFoundException;

    public Command parse(String cmd) throws WrongCommandFormatException, CommandNotFoundException {
        return handle(cmd);
    }
}
