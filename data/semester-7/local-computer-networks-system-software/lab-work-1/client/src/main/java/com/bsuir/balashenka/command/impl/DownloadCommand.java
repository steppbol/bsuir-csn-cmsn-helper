package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ClientController;
import com.bsuir.balashenka.controller.impl.TcpClientController;
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
    private static final String SESSION_KEY = "session_key";
    private static final String TOKEN_FILE_PATH = "client\\src\\main\\resources\\token.txt";
    private static final int FILE_BUFF_SIZE = 500_000;
    private static final int BYTES_PER_MEBIBYTE = 1_048_576;
    private ClientController clientController;

    public DownloadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
        this.clientController = TcpClientController.getInstance();
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
                executeDownload();
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

        for (AvailableToken token : AvailableToken.values()) {
            if ((token.getName().equals("path") || token.getName().equals("name")) && token.isRequired()) {
                String value = tokens.get(token.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + token.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   download -path='path to file' -name='file name' [-help]");
    }

    private void executeDownload() {
        Connection tcpConnection = clientController.getConnection();

        if (tcpConnection != null) {
            String filePath = getTokens().get(AvailableToken.PATH.getName());
            File tokenFile = new File(TOKEN_FILE_PATH);
            try {
                FileInputStream fileInputStream = new FileInputStream(tokenFile);
                Scanner scanner = new Scanner(fileInputStream);
                if (scanner.hasNextLine()) {
                    String tokenInfo = scanner.nextLine();
                    System.out.println("Token from file: " + tokenInfo);
                    if (tokenInfo.startsWith(filePath)) {
                        tcpConnection.send(getCommand() + " -" + SESSION_KEY + "='" + tokenInfo.substring(filePath.length() + 1) + "'");
                    } else {
                        tcpConnection.send(getCommand());
                    }
                } else {
                    tcpConnection.send(getCommand());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String[] confirmation = tcpConnection.receive().split(" ");

            if (SUCCESS.equals(confirmation[0])) {
                try {
                    if (confirmation.length > 2) {
                        FileOutputStream tokenFileOutputStream = new FileOutputStream(TOKEN_FILE_PATH);
                        tokenFileOutputStream.write((filePath + " " + confirmation[2]).getBytes());
                        tokenFileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final long fileSize = Long.parseLong(confirmation[1]);
                long receivedBytes = 0;
                try {
                    File file = new File(getTokens().get(AvailableToken.NAME.getName()));
                    if (file.exists()) {
                        receivedBytes = file.length();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    fileOutputStream.getChannel().position(receivedBytes);
                    tcpConnection.send(START_TRANSFER + " " + receivedBytes);

                    System.out.println("Starting from: " + receivedBytes);
                    byte[] fileBuffer = new byte[FILE_BUFF_SIZE];
                    int bufferPosition = 0;
                    int count;
                    long startTime = System.currentTimeMillis();
                    while ((count = tcpConnection.receive(fileBuffer, bufferPosition, FILE_BUFF_SIZE - bufferPosition)) != -1) {
                        receivedBytes += count;
                        bufferPosition += count;
                        if (bufferPosition == FILE_BUFF_SIZE) {
                            fileOutputStream.write(Arrays.copyOfRange(fileBuffer, 0, FILE_BUFF_SIZE));
                            bufferPosition = 0;
                        }
                        if (receivedBytes == fileSize) {
                            break;
                        }

                        System.out.print("\r" + (int) (((double) receivedBytes / fileSize) * 100) + "%");
                    }
                    if (bufferPosition != 0) {
                        fileOutputStream.write(Arrays.copyOfRange(fileBuffer, 0, bufferPosition));
                    }
                    fileOutputStream.close();
                    System.out.println("\r100%");

                    FileOutputStream fileOutputStreamToEmpty = new FileOutputStream(TOKEN_FILE_PATH);
                    fileOutputStreamToEmpty.close();

                    long resultTimeInSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
                    System.out.println("File is downloaded. Total size: " + receivedBytes + " bytes.");
                    System.out.println(String.format("Total time: %d s", resultTimeInSeconds));
                    System.out.println(String.format("Bandwidth: %.3f MiB/s", (((double) receivedBytes) / resultTimeInSeconds / BYTES_PER_MEBIBYTE)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("You're not connected to server.");
        }
    }
}
