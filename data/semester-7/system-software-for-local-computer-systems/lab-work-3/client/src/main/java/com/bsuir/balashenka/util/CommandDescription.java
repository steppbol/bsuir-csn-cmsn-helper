package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;

public class CommandDescription {
    private String commandName;
    private String description;

    private Command command;

    public CommandDescription(String commandName, String description, Command command) {
        this.commandName = commandName;
        this.description = description;
        this.command = command;
    }

    public String getName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    public Command getCommand() {
        return command.buildCommandInstance();
    }
}
