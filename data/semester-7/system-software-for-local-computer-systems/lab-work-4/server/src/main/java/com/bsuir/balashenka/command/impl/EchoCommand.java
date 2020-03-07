package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.service.DefaultServerConnection;
import com.bsuir.balashenka.util.AvailableToken;

import java.io.IOException;
import java.util.Arrays;

public class EchoCommand extends BaseCommand {

    public EchoCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute(DefaultServerConnection connection) {
        try {
            String content = getTokens().get(AvailableToken.CONTENT.getName());

            if (content != null) {
                executeEcho(content, connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new EchoCommand();
    }

    private void executeEcho(String content, DefaultServerConnection connection) throws IOException {
        connection.write(content);
    }
}
