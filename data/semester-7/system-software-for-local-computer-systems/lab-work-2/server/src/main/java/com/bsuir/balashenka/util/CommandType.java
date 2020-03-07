package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.command.impl.DownloadCommand;
import com.bsuir.balashenka.command.impl.EchoCommand;
import com.bsuir.balashenka.command.impl.TimeCommand;
import com.bsuir.balashenka.command.impl.UploadCommand;
import com.bsuir.balashenka.exception.CommandNotFoundException;

public enum CommandType {
    ECHO("echo", new EchoCommand()),
    TIME("time", new TimeCommand()),
    DOWNLOAD("download", new DownloadCommand()),
    UPLOAD("upload", new UploadCommand());

    private String commandName;
    private Command command;

    CommandType(String commandName, Command command) {
        this.commandName = commandName;
        this.command = command;
    }

    public static Command findCommand(String commandName) throws CommandNotFoundException {
        for (CommandType type : CommandType.values()) {
            if (type.getName().equals(commandName)) {
                return type.getCommand();
            }
        }
        throw new CommandNotFoundException("Cannot find command by name[=" + commandName + "]");
    }

    public static boolean hasCommand(String commandName) {
        for (CommandType type : CommandType.values()) {
            if (type.getName().equals(commandName)) {
                return true;
            }
        }
        return false;
    }


    public String getName() {
        return commandName;
    }

    public Command getCommand() {
        return command.build();
    }
}