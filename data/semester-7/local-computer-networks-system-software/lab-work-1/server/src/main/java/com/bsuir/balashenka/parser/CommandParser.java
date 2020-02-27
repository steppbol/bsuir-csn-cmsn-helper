package com.bsuir.balashenka.parser;

import com.bsuir.balashenka.command.Command;

public interface CommandParser {
    Command handle(String command) throws Exception;

    default Command parse(String command) throws Exception {
        return handle(command);
    }
}
