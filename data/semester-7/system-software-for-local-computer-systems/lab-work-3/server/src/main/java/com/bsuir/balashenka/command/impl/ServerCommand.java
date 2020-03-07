package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.service.ClientConnection;
import com.bsuir.balashenka.util.CommandToken;

public abstract class ServerCommand extends BaseCommand {
    public ServerCommand(CommandToken... commandTokens) {
        super(commandTokens);
    }

    @Override
    final public void execute() throws CannotExecuteCommandException {
        throw new CannotExecuteCommandException("No connection, use method execute with connection as parameter.");
    }

    public abstract void execute(ClientConnection connection) throws CannotExecuteCommandException;
}