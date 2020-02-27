package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandRegister {
    private static CommandRegister instance;
    private ArrayList<CommandDescription> commandDescriptions = new ArrayList<>();

    private CommandRegister(CommandDescription... commands) {
        commandDescriptions.addAll(Arrays.asList(commands));
    }

    public static void init(CommandDescription... commands) {
        if (instance == null) {
            instance = new CommandRegister(commands);
        }
    }

    public static CommandRegister getInstance() {
        return instance;
    }

    public ArrayList<CommandDescription> getCommands() {
        return commandDescriptions;
    }

    public Command findCommand(String commandName) throws CommandNotFoundException {
        for (CommandDescription description : commandDescriptions) {
            if (description.getName().equals(commandName)) {
                return description.getCommand();
            }
        }
        throw new CommandNotFoundException("Cannot find command by name[=" + commandName + "]");
    }

    public boolean hasCommand(String commandName) {
        for (CommandDescription description : commandDescriptions) {
            if (description.getName().equals(commandName)) {
                return true;
            }
        }
        return false;
    }
}
