package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.service.ClientConnection;
import com.bsuir.balashenka.util.CommandToken;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class DownloadCommand extends ServerCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";
    private static final int BUFFER_SIZE = 500_000;
    private static final CommandToken COMMAND_PATH = new CommandToken("path", "^[\\w .-:\\\\]+$", true);
    private static final CommandToken COMMAND_NAME = new CommandToken("name", "^[\\w .-:\\\\]+$", true);
    private FileInputStream fileInputStream;
    private boolean isStarted;
    private byte[] fileContent;
    private long sentBytes;
    private long startTime;
    private long endTime;

    public DownloadCommand() {
        super(COMMAND_PATH,
                COMMAND_NAME);
        fileContent = new byte[BUFFER_SIZE];
    }

    @Override
    public void execute(ClientConnection connection) {
        try {
            String path = getAllTokens().get(COMMAND_PATH.getName());
            if (path != null) {
                executeDownload(connection, path);
            }
        } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command buildCommandInstance() {
        return new DownloadCommand();
    }

    private void executeDownload(ClientConnection connection, String path) throws IOException, InterruptedException, TimeoutException {
        if (connection != null) {
            if (!isStarted) {
                File file = new File(path);
                long fileSize = file.length();
                if (file.exists() && !file.isDirectory()) {
                    sentBytes = 0;
                    connection.write(SUCCESS + " " + fileSize);
                    String startMessage = connection.read(5000);
                    String[] startMessageFragments = startMessage.split(" ");
                    sentBytes = Integer.parseInt(startMessageFragments[1]);
                    if (START_TRANSFER.equals(startMessageFragments[0])) {
                        fileInputStream = new FileInputStream(file);
                        log.info("Bytes were sent: " + sentBytes);
                        long skippedBytes = fileInputStream.skip(sentBytes);
                        if (skippedBytes != sentBytes) {
                            log.warn("Expected skipped bytes {} and actual skipped bytes {}", sentBytes, skippedBytes);
                        }
                        startTime = System.currentTimeMillis();
                        isStarted = true;
                    }
                } else {
                    String message = "File does not exists or something went wrong.";
                    connection.write(message);
                    log.info(message);
                    setDone(true);
                }
            } else {
                int receivedBytes;
                if ((receivedBytes = fileInputStream.read(fileContent, 0, BUFFER_SIZE)) != -1) {
                    connection.write(fileContent, receivedBytes);
                    sentBytes += receivedBytes;
                    Thread.sleep(1);
                    log.info("Sent " + receivedBytes + " bytes.");
                } else {
                    endTime = System.currentTimeMillis();

                    log.info("File is transferred.");

                    long resultTime = endTime - startTime;
                    long resultTimeInSeconds = TimeUnit.SECONDS.convert(resultTime, TimeUnit.MILLISECONDS);
                    log.info("Transfer time: " + ((resultTimeInSeconds > 0) ? resultTimeInSeconds + "s" : resultTime + "ms"));
                    setDone(true);
                }
            }
        } else {
            log.info("You're not connected to server.");
            setDone(true);
        }
    }
}
