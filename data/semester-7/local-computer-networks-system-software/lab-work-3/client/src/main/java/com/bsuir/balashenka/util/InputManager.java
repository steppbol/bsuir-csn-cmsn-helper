package com.bsuir.balashenka.util;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.exception.CommandNotFoundException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.parser.impl.CommandParser;

import java.util.Scanner;

public class InputManager {
    private static final String LOCAL_SERVER_IP = "127.0.0.1";
    private static final String REMOTE_SERVER_IP = "192.168.100.4";
    private static final String COMMAND_CONNECT_TCP = "connect -ip='%s' -p='tcp'";
    private static final String COMMAND_CONNECT_TCP_LOCAL = String.format(COMMAND_CONNECT_TCP, LOCAL_SERVER_IP);
    private static final String COMMAND_CONNECT_TCP_REMOTE = String.format(COMMAND_CONNECT_TCP, REMOTE_SERVER_IP);
    private static final String COMMAND_CONNECT_UDP = "connect -ip='%s' -p='udp'";
    private static final String COMMAND_CONNECT_UDP_LOCAL = String.format(COMMAND_CONNECT_UDP, LOCAL_SERVER_IP);
    private static final String COMMAND_CONNECT_UDP_REMOTE = String.format(COMMAND_CONNECT_UDP, REMOTE_SERVER_IP);
    private Scanner scanner;
    private boolean isWantExit;

    public InputManager() {
        scanner = new Scanner(System.in);
        isWantExit = false;
    }

    public Command getCommand() throws WrongCommandFormatException, CommandNotFoundException {
        String cmd = scanner.nextLine();
        String commandFromAbbreviature = null;
        switch (cmd) {
            case "ltcp":
                commandFromAbbreviature = COMMAND_CONNECT_TCP_LOCAL;
                break;
            case "tcp":
                commandFromAbbreviature = COMMAND_CONNECT_TCP_REMOTE;
                break;
            case "ludp":
                commandFromAbbreviature = COMMAND_CONNECT_UDP_LOCAL;
                break;
            case "udp":
                commandFromAbbreviature = COMMAND_CONNECT_UDP_REMOTE;
                break;
        }
        if (commandFromAbbreviature != null) {
            System.out.println("\r" + commandFromAbbreviature);
            cmd = commandFromAbbreviature;
        }
        return new CommandParser(CommandRegister.getInstance()).parse(cmd);
    }

    public void wantExit(boolean want) {
        isWantExit = want;
    }

    public boolean enteredExit() {
        return isWantExit;
    }
}
