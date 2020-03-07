package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.util.CommandDescription;
import com.bsuir.balashenka.util.CommandRegister;

import java.util.ArrayList;

public class HelpCommand extends BaseCommand {
    @Override
    public void execute() {
        ArrayList<CommandDescription> commandDescriptions = CommandRegister.getInstance().getCommands();

        System.out.println("The most commonly used client commands are:");
        commandDescriptions.forEach((commandDescription -> System.out.println("  " + commandDescription.getName() + " - " + commandDescription.getDescription())));
    }

    @Override
    public Command buildCommandInstance() {
        return new HelpCommand();
    }
}
