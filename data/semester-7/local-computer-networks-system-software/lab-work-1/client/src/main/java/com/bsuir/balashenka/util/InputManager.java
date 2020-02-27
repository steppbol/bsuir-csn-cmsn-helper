package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.parser.impl.DefaultCommandParser;

import java.util.Scanner;

public class InputManager {
    private Scanner scanner;
    private boolean isWantExit;

    public InputManager() {
        scanner = new Scanner(System.in);
        isWantExit = false;
    }

    public Command getCommand() throws WrongCommandFormatException, CommandNotFoundException {
        String cmd = scanner.nextLine();
        return new DefaultCommandParser().parse(cmd);
    }

    public void wantExit(boolean want) {
        isWantExit = want;
    }

    public boolean enteredExit() {
        return isWantExit;
    }
}
