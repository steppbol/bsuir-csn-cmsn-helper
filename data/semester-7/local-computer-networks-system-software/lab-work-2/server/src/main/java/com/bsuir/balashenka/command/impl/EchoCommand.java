package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ServerController;
import com.bsuir.balashenka.controller.impl.DefaultServerController;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableToken;

import java.io.IOException;
import java.util.Arrays;

public class EchoCommand extends BaseCommand {
    private ServerController serverController;

    public EchoCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
        serverController = DefaultServerController.getInstance();
    }

    @Override
    public void execute() {
        try {
            String content = getAllTokens().get(AvailableToken.CONTENT.getName());
            if (content != null) {
                Connection connection = serverController.getConnection();
                connection.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new EchoCommand();
    }
}
