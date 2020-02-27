package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.AvailableTokenNotPresentException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class UploadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";
    private static final String SESSION_KEY = "session_key";
    private static final String TOKEN_FILE_PATH = "client\\src\\main\\resources\\token.txt";
    private static final int FILE_BUFF_SIZE = 500_000;
    private static final int BYTES_PER_MEBIBYTE = 1_048_576;

    public UploadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            Map<String, String> tokens = getTokens();

            String firstKey = String.valueOf(tokens.keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            if (currentToken == AvailableToken.HELP) {
                executeHelp();
            } else {
                executeUpload();
            }
        } catch (WrongCommandFormatException | AvailableTokenNotPresentException e) {
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
        Connection tcpConnection = Controller.getInstance().getConnection();
        if (tcpConnection != null) {
            String filePath = getTokens().get(AvailableToken.PATH.getName());
            File tokenFile = new File(TOKEN_FILE_PATH);
            try {
                FileInputStream fin = new FileInputStream(tokenFile);
                Scanner scanner = new Scanner(fin);
                if (scanner.hasNextLine()) {
                    String tokenInfo = scanner.nextLine();
                    System.out.println("Token from file: " + tokenInfo);
                    if (tokenInfo.startsWith(filePath)) {
                        tcpConnection.sendMessage(getCommand() + " -" + SESSION_KEY + "='" + tokenInfo.substring(filePath.length() + 1) + "'");
                    } else {
                        tcpConnection.sendMessage(getCommand());
                    }
                } else {
                    tcpConnection.sendMessage(getCommand());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            File file = new File(filePath);
            final long fileSize = file.length();
            tcpConnection.sendMessage(SUCCESS + " " + fileSize);

            if (file.exists() && !file.isDirectory()) {
                String startMessage = tcpConnection.receive();
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
                            tcpConnection.sendMessage(fileContent, receivedBytes);
                            sentBytes += receivedBytes;
                            Thread.sleep(1);
                            System.out.print("\r" + (int) (((double) sentBytes / fileSize) * 100) + "%");
                        }

                        System.out.println("\nFile is transferred");

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
                tcpConnection.sendMessage(message);
                System.out.println(message);
            }
        }
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
            if ((t.getName().equals("path") || t.getName().equals("name")) && t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }
}
