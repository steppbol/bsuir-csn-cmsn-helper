package com.bsuir.balashenka.parser;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;

public interface Parser {
    Command parse(String command) throws WrongCommandFormatException, CommandNotFoundException;
}
