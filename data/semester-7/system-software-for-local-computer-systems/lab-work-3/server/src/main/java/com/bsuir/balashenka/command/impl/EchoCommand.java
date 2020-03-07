package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.service.ClientConnection;
import com.bsuir.balashenka.util.CommandToken;

import java.io.IOException;

public class EchoCommand extends ServerCommand {
    private static final CommandToken COMMAND_CONTENT = new CommandToken("content", null, true);

    public EchoCommand() {
        super(COMMAND_CONTENT);
    }

    @Override
    public void execute(ClientConnection connection) {
        try {
            String content = getAllTokens().get(COMMAND_CONTENT.getName());
            if (content != null) {
                executeEcho(connection, content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setDone(true);
    }

    @Override
    public Command buildCommandInstance() {
        return new EchoCommand();
    }

    private void executeEcho(ClientConnection connection, String content) throws IOException {
        connection.write(content);
    }
}
