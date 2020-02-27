package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.command.impl.*;
import com.bsuir.balashenka.exception.CommandNotFoundException;

public enum CommandType {
    CONNECT("connect", "Connect to server", new ConnectCommand()),
    DISCONNECT("disconnect", "Disconnect from server", new DisconnectCommand()),
    DOWNLOAD("download", "Download file from server", new DownloadCommand()),
    UPLOAD("upload", "Upload file to server", new UploadCommand()),
    ECHO("echo", "Check server echo", new EchoCommand()),
    TIME("time", "Get server time", new TimeCommand()),
    HELP("help", "Display help information about available commands", new HelpCommand()),
    EXIT("exit", "Terminate program", new ExitCommand());

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
