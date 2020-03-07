package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.service.DefaultServerConnection;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DownloadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";

    private static final int BUFF_SIZE = 12288;

    public DownloadCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> getAvailableTokens().put(t.getName(), t.getRegex()));
    }

    @Override
    public void execute(DefaultServerConnection connection) {
        try {
            String path = getTokens().get(AvailableToken.PATH.getName());

            if (path != null) {
                executeDownload(connection, path);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command build() {
        return new DownloadCommand();
    }

    private void executeDownload(DefaultServerConnection connection, String path) throws IOException, InterruptedException {
        if (connection != null) {
            File file = new File(path);

            final long fileSize = file.length();

            if (file.exists() && !file.isDirectory()) {
                connection.write(SUCCESS + " " + fileSize);

                if (START_TRANSFER.equals(connection.read())) {
                    FileInputStream fin = new FileInputStream(file);

                    int receivedBytes;
                    byte fileContent[] = new byte[BUFF_SIZE];

                    Date start = new Date();

                    while ((receivedBytes = fin.read(fileContent, 0, BUFF_SIZE)) != -1) {
                        connection.write(fileContent, receivedBytes);
                        Thread.sleep(1);
                        log.debug("Sent " + receivedBytes + " bytes.");
                    }

                    Date end = new Date();
                    long resultTime = end.getTime() - start.getTime();

                    log.info("File is transferred.");
                    long resultTimeInSeconds = TimeUnit.SECONDS.convert(resultTime, TimeUnit.MILLISECONDS);
                    log.info("Transfer time: " + ((resultTimeInSeconds > 0) ? resultTimeInSeconds + "s" : resultTime + "ms"));
                } else {
                    log.info(START_TRANSFER + " flag not founded...");
                }
            } else {
                final String message = "File does not exists or something went wrong.";
                connection.write(message);
                log.error(message);
            }
        }
    }

    private enum AvailableToken {
        PATH("path", "^[\\w .-:\\\\]+$"),
        NAME("name", "^[\\w .-:\\\\]+$");

        private String name;
        private String regex;

        AvailableToken(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }
    }
}
