package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.util.CommandType;

import java.util.HashMap;

public class HelpCommand extends BaseCommand {
    @Override
    public void execute() {
        HashMap<String, String> commands = new HashMap<>();

        for (CommandType type : CommandType.values()) {
            commands.put(type.getName(), type.getDescription());
        }

        commands.forEach((k, v) -> {
            System.out.println("  " + k + " - " + v);
        });
    }

    @Override
    public Command build() {
        return new HelpCommand();
    }
}
