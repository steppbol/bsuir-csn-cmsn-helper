package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.AvailableTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableToken;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class DownloadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";

    private static final int BUFF_SIZE = 12288;

    public DownloadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            Map<String, String> toks = getTokens();

            String firstKey = String.valueOf(toks.keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            switch (currentToken) {
                case HELP:
                    executeHelp();
                    break;
                default:
                    executeDownload();
                    break;
            }
        } catch (WrongCommandFormatException | AvailableTokenNotPresentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new DownloadCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getTokens();

        if (tokens.size() > 2) {
            throw new WrongCommandFormatException("This command should have only one or two tokens.");
        }

        if (tokens.containsKey(AvailableToken.HELP.getName())) {
            return;
        }

        for (AvailableToken t : AvailableToken.values()) {
            if (t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   download -path='path to file' -name='file name' [-help]");
    }

    private void executeDownload() {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            if (connection.sendMessage(getCommand())) {
                String[] confirmation = connection.receive().split(" ");

                if (SUCCESS.equals(confirmation[0])) {
                    final long fileSize = Long.parseLong(confirmation[1]);
                    System.out.println("File size: " + fileSize + " bytes");

                    if (connection.sendMessage(START_TRANSFER)) {
                        long receivedBytes = 0;
                        final long middleOfFileSize = fileSize / 2;
                        boolean had50Percents = false;

                        try {
                            FileOutputStream fos = new FileOutputStream(getTokens().get(AvailableToken.NAME.getName()));

                            byte[] buff = new byte[BUFF_SIZE];
                            int count;

                            while ((count = connection.receive(buff)) != -1) {
                                receivedBytes += count;
                                fos.write(Arrays.copyOfRange(buff, 0, count));

                                System.out.println("Received " + receivedBytes + " bytes.");
                                Thread.sleep(4);

                                if (receivedBytes == fileSize) {
                                    break;
                                }

                                if (receivedBytes >= middleOfFileSize && !had50Percents) {
                                    System.out.println("Received 50%...");
                                    had50Percents = true;
                                }
                            }

                            fos.close();
                            System.out.println("File is downloaded. Total size: " + receivedBytes + " bytes.");
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            System.out.println("You're not connected to server.");
        }
    }

    private enum AvailableToken {
        PATH("path", "^[\\w .-:\\\\]+$", true),
        NAME("name", "^[\\w .-:\\\\]+$", true),
        HELP("help", null, false);

        private String name;
        private String regex;
        private boolean required;

        AvailableToken(String name, String regex, boolean required) {
            this.name = name;
            this.regex = regex;
            this.required = required;
        }

        public static AvailableToken find(String name) throws AvailableTokenNotPresentException {
            for (AvailableToken t : values()) {
                if (t.getName().equals(name)) {
                    return t;
                }
            }

            throw new AvailableTokenNotPresentException("Token '" + name + "' is not available.");
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
