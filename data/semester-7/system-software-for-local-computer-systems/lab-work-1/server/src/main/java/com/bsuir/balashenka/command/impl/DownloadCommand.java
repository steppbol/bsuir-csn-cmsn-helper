package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ServerController;
import com.bsuir.balashenka.controller.impl.TcpServerController;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.AvailableToken;
import com.bsuir.balashenka.util.TokenGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DownloadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";
    private static final int BUFF_SIZE = 500_000;
    private Map<String, Long> filesTokens;
    private ServerController serverController;

    public DownloadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
        filesTokens = new HashMap<>();
        serverController = TcpServerController.getInstance();
    }

    @Override
    public void execute() {
        try {
            String path = getAllTokens().get(AvailableToken.PATH.getName());
            if (path != null) {
                executeDownload(path);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new DownloadCommand();
    }

    private void executeDownload(String path) throws IOException, InterruptedException {
        Connection tcpConnection = serverController.getConnection();
        if (tcpConnection != null) {
            String clientToken = getAllTokens().get(AvailableToken.SESSION_KEY.getName());
            if (clientToken != null) {
                log.info("Client token {}", clientToken);
            } else {
                log.warn("No client token");
            }

            File file = new File(path);
            final long fileSize = file.length();
            if (file.exists() && !file.isDirectory()) {
                String token;
                if (clientToken != null && filesTokens.containsKey(clientToken)) {
                    log.info("Have old token {}", clientToken);
                    tcpConnection.write(SUCCESS + " " + fileSize);
                    token = clientToken;
                } else {
                    token = TokenGenerator.generate();
                    log.info("Generate new token {}", token);
                    filesTokens.put(token, 0L);
                    tcpConnection.write(SUCCESS + " " + fileSize + " " + TokenGenerator.generate());
                }
                String startMessage = tcpConnection.read();
                String[] startMessageFragments = startMessage.split(" ");
                long sentBytes = Integer.parseInt(startMessageFragments[1]);
                if (START_TRANSFER.equals(startMessageFragments[0])) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    log.info("Bytes were sent: {}", sentBytes);
                    long skippedBytes = fileInputStream.skip(sentBytes);
                    if (skippedBytes != sentBytes) {
                        log.warn("Expected skipped bytes {} and actual skipped bytes {}", sentBytes, skippedBytes);
                    }

                    byte[] fileContent = new byte[BUFF_SIZE];
                    Date start = new Date();
                    int receivedBytes;
                    while ((receivedBytes = fileInputStream.read(fileContent, 0, BUFF_SIZE)) != -1) {
                        tcpConnection.write(fileContent, receivedBytes);
                        sentBytes += receivedBytes;
                        filesTokens.put(token, sentBytes);
                        Thread.sleep(1);
                    }
                    filesTokens.remove(token);
                    log.info("File is transferred");

                    Date end = new Date();
                    long resultTime = end.getTime() - start.getTime();
                    long resultTimeInSeconds = TimeUnit.SECONDS.convert(resultTime, TimeUnit.MILLISECONDS);
                    log.info("Transfer time: {}", ((resultTimeInSeconds > 0) ? resultTimeInSeconds + "s" : resultTime + "ms"));
                } else {
                    log.info("{} flag not founded...", START_TRANSFER);
                }
            } else {
                String message = "File does not exists or something went wrong";
                tcpConnection.write(message);
                log.warn(message);
            }
        }
    }
}

