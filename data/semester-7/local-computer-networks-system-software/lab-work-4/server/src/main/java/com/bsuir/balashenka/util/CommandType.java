package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.command.impl.DownloadCommand;
import com.bsuir.balashenka.command.impl.EchoCommand;
import com.bsuir.balashenka.command.impl.TimeCommand;
import com.bsuir.balashenka.command.impl.UploadCommand;
import com.bsuir.balashenka.exception.CommandNotFoundException;

public enum CommandType {
    ECHO("echo", "Echo server", new EchoCommand()),
    TIME("time", "Get server time", new TimeCommand()),
    DOWNLOAD("download", "Download file from server", new DownloadCommand()),
    UPLOAD("upload", "Upload file to server", new UploadCommand());

    private String commandName;
    private String description;
    private Command command;

    CommandType(String commandName, String description, Command command) {
        this.commandName = commandName;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public Command getCommand() {
        return command.build();
    }
}
