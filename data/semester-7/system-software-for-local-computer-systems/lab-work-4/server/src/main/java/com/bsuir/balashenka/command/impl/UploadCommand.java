package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ServerController;
import com.bsuir.balashenka.service.DefaultServerConnection;
import com.bsuir.balashenka.util.AvailableToken;
import com.bsuir.balashenka.util.TokenGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UploadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";
    private static final int BUFF_SIZE = 500_000;
    private static final int BYTES_PER_MEBIBYTE = 1_048_576;
    private Map<String, Long> filesTokens;
    private ServerController serverController;

    public UploadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
        filesTokens = new HashMap<>();
    }

    @Override
    public void execute(DefaultServerConnection connection) {
        try {
            executeUpload(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new UploadCommand();
    }

    private void executeUpload(DefaultServerConnection connection) throws IOException {
        if (connection != null) {
            String clientToken = getTokens().get(AvailableToken.SESSION_KEY.getName());
            if (clientToken != null) {
                log.info("Client token {}", clientToken);
            } else {
                log.warn("No client token");
            }

            if (clientToken != null && filesTokens.containsKey(clientToken)) {
                log.info("Have old token {}", clientToken);
            } else {
                String token = TokenGenerator.generate();
                log.info("Generate new token {}", token);
                filesTokens.put(token, 0L);
            }

            String[] confirmation = connection.read().split(" ");
            File file = new File(getTokens().get(AvailableToken.NAME.getName()));
            final long fileSize = Long.parseLong(confirmation[1]);
            if (SUCCESS.equals(confirmation[0])) {
                try {
                    long receivedBytes = 0;
                    if (file.exists()) {
                        receivedBytes = file.length();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(file, true);
                    fileOutputStream.getChannel().position(receivedBytes);
                    connection.write(START_TRANSFER + " " + receivedBytes);
                    log.info("Starting from bytes: " + receivedBytes);
                    long startTime = System.currentTimeMillis();
                    int count;
                    byte[] fileBuffer = new byte[BUFF_SIZE];
                    int bufferPosition = 0;
                    while ((count = connection.read(fileBuffer, bufferPosition, BUFF_SIZE - bufferPosition)) != -1) {
                        receivedBytes += count;
                        bufferPosition += count;
                        if (bufferPosition == BUFF_SIZE) {
                            fileOutputStream.write(Arrays.copyOfRange(fileBuffer, 0, BUFF_SIZE));
                            bufferPosition = 0;
                        }

                        if (receivedBytes == fileSize) {
                            break;
                        }
                    }
                    if (bufferPosition != 0) {
                        fileOutputStream.write(Arrays.copyOfRange(fileBuffer, 0, bufferPosition));
                    }
                    fileOutputStream.close();
                    log.info((int) (((double) receivedBytes / fileSize) * 100) + "%");

                    long resultTimeInSeconds = TimeUnit.SECONDS.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS);
                    log.info("File is downloaded. Total size: {} bytes", receivedBytes);
                    log.info(String.format("Total time: %d s", resultTimeInSeconds));
                    log.info(String.format("Bandwidth: %.3f MiB/s",
                            (((double) receivedBytes) / resultTimeInSeconds / BYTES_PER_MEBIBYTE)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
