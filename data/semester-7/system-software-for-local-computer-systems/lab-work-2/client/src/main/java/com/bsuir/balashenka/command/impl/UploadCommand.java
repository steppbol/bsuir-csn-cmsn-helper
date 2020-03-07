package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.controller.impl.ClientController;
import com.bsuir.balashenka.exception.CommandTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableCommandToken;
import com.bsuir.balashenka.util.CommandToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class UploadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";
    private static final String SESSION_KEY = "session_key";

    private static final int FILE_BUFF_SIZE = 500_000;
    private static final int BYTES_PER_MEBIBYTE = 1_048_576;
    private static final String TOKEN_FILE_PATH = "client\\src\\main\\resources\\token.txt";
    private final CommandToken COMMAND_PATH = new CommandToken("path", "^[\\w .-:\\\\]+$", true);
    private final CommandToken COMMAND_NAME = new CommandToken("name", "^[\\w .-:\\\\]+$", true);
    private final CommandToken COMMAND_HELP = new CommandToken("help", null, false);
    private Controller clientController;
    private AvailableCommandToken availableCommandTokens = new AvailableCommandToken();

    public UploadCommand() {
        availableCommandTokens.addToken(COMMAND_PATH);
        availableCommandTokens.addToken(COMMAND_NAME);
        availableCommandTokens.addToken(COMMAND_HELP);
        clientController = ClientController.getInstance();
        availableCommandTokens.getTokens().forEach((t) -> {
            getAvailableTokens().put(t.getName(), t.getRegex());
        });
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            Map<String, String> tokens = getAllTokens();

            String firstKey = String.valueOf(tokens.keySet().toArray()[0]);
            CommandToken currentToken = availableCommandTokens.find(firstKey);

            if (currentToken.equals(COMMAND_HELP)) {
                executeHelp();
            } else {
                executeUpload();
            }
        } catch (WrongCommandFormatException | CommandTokenNotPresentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new UploadCommand();
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   upload -path='path to file' -name='file name' [-help]");
    }

    private void executeUpload() {
        Connection connection = clientController.getConnection();
        if (connection != null) {
            String filePath = getAvailableTokens().get(COMMAND_PATH.getName());
            File tokenFile = new File(TOKEN_FILE_PATH);
            try {
                FileInputStream fin = new FileInputStream(tokenFile);
                Scanner scanner = new Scanner(fin);
                if (scanner.hasNextLine()) {
                    String tokenInfo = scanner.nextLine();
                    System.out.println("Token from file: " + tokenInfo);
                    if (tokenInfo.startsWith(filePath)) {
                        connection.send(getCommand() + " -" + SESSION_KEY + "='" + tokenInfo.substring(filePath.length() + 1) + "'");
                    } else {
                        connection.send(getCommand());
                    }
                } else {
                    connection.send(getCommand());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File file = new File(filePath);
            final long fileSize = file.length();
            connection.send(SUCCESS + " " + fileSize);

            if (file.exists() && !file.isDirectory()) {
                String startMessage = null;
                try {
                    startMessage = connection.receive();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                }
                String[] startMessageFragments = startMessage.split(" ");
                long sentBytes = Integer.parseInt(startMessageFragments[1]);
                try {
                    if (START_TRANSFER.equals(startMessageFragments[0])) {
                        FileInputStream fileInputStream = new FileInputStream(file);

                        System.out.println("Bytes were sent: " + sentBytes);

                        long skippedBytes = fileInputStream.skip(sentBytes);
                        if (skippedBytes != sentBytes) {
                            System.out.println("Expected skipped bytes " + sentBytes + "and actual skipped bytes " + skippedBytes);
                        }

                        Date startTime = new Date();
                        byte[] fileContent = new byte[FILE_BUFF_SIZE];
                        int receivedBytes;
                        while ((receivedBytes = fileInputStream.read(fileContent, 0, FILE_BUFF_SIZE)) != -1) {
                            connection.send(fileContent, receivedBytes);
                            sentBytes += receivedBytes;
                            Thread.sleep(1);
                            System.out.print("\r" + (int) (((double) sentBytes / fileSize) * 100) + "%");
                        }

                        System.out.println("File is transferred");

                        Date endTime = new Date();
                        long resultTime = endTime.getTime() - startTime.getTime();
                        long resultTimeInSeconds = TimeUnit.SECONDS.convert(resultTime, TimeUnit.MILLISECONDS);
                        System.out.println("File is upload. Total size: " + sentBytes + " bytes.");
                        System.out.println("Transfer time: " + ((resultTimeInSeconds > 0) ? resultTimeInSeconds + "s" : resultTime + "ms"));
                        System.out.println(String.format("Bandwidth: %.3f MiB/s", (((double) sentBytes) / resultTimeInSeconds / BYTES_PER_MEBIBYTE)));
                    } else {
                        System.out.println(START_TRANSFER + " flag not founded...");
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                String message = "File does not exists or something went wrong";
                connection.send(message);
                System.out.println(message);
            }
        }
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getAllTokens();

        if (tokens.size() > 2) {
            throw new WrongCommandFormatException("This command should have only one or two tokens.");
        }

        if (tokens.containsKey(COMMAND_HELP.getName())) return;

        for (CommandToken t : availableCommandTokens.getTokens()) {
            if (t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }
}
