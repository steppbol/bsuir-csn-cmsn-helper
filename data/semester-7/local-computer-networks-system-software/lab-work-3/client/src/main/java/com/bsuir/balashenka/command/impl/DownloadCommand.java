package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.Controller;
import com.bsuir.balashenka.exception.CannotExecuteCommandException;
import com.bsuir.balashenka.exception.WrongCommandFormatException;
import com.bsuir.balashenka.service.Connection;
import com.bsuir.balashenka.util.CommandToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DownloadCommand extends BaseCommand {
    private static final String SUCCESS = "success";
    private static final String START_TRANSFER = "start";
    private static final int FILE_BUFF_SIZE = 500_000;
    private static final int BYTES_PER_MEBIBYTE = 1_048_576;
    private static final CommandToken COMMAND_PATH = new CommandToken("path", "^[\\w .-:\\\\]+$", true);
    private static final CommandToken COMMAND_NAME = new CommandToken("name", "^[\\w .-:\\\\]+$", true);
    private static final CommandToken COMMAND_HELP = new CommandToken("help", null, false);

    public DownloadCommand() {
        super(COMMAND_PATH,
                COMMAND_NAME,
                COMMAND_HELP);
    }

    @Override
    public void execute() throws CannotExecuteCommandException {
        try {
            validateRequiredTokens();
            validateTokensByRegex();

            Map<String, String> tokens = getAllTokens();

            if (tokens.containsKey(COMMAND_HELP.getName())) {
                executeHelp();
            } else {
                executeDownload();
            }
        } catch (WrongCommandFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Command buildCommandInstance() {
        return new DownloadCommand();
    }

    private void executeHelp() {
        System.out.println("Command format:");
        System.out.println("   download -path='path to file' -name='file name' [-help]");
    }

    private void executeDownload() throws CannotExecuteCommandException {
        Connection connection = Controller.getInstance().getConnection();
        connection.sendMessage(getCommand());

        String[] confirmation;
        try {
            confirmation = connection.read().split(" ");
        } catch (SocketTimeoutException e) {
            throw new CannotExecuteCommandException(e);
        }

        if (SUCCESS.equals(confirmation[0])) {
            final long fileSize = Long.parseLong(confirmation[1]);
            System.out.println("File size: " + fileSize);

            long receivedBytes = 0;
            int currPercent = 0;

            try {
                File file = new File(getAllTokens().get(COMMAND_NAME.getName()));
                if (file.exists()) {
                    receivedBytes = file.length();
                }
                FileOutputStream fos = new FileOutputStream(file, true);
                fos.getChannel().position(receivedBytes);
                connection.sendMessage(START_TRANSFER + " " + receivedBytes);

                byte[] fileBuff = new byte[FILE_BUFF_SIZE];
                int buffPosition = 0;
                int count;
                long startTime, endTime;

                System.out.println("Starting from: " + receivedBytes);
                startTime = System.currentTimeMillis();

                while (receivedBytes != fileSize &&
                        (count = connection.read(fileBuff, buffPosition, FILE_BUFF_SIZE - buffPosition)) != -1) {
                    receivedBytes += count;
                    buffPosition += count;
                    if (buffPosition == FILE_BUFF_SIZE) {
                        fos.write(Arrays.copyOfRange(fileBuff, 0, FILE_BUFF_SIZE));
                        buffPosition = 0;
                    }

                    if (receivedBytes == fileSize) {
                        break;
                    }

                    currPercent = (int) (((double) receivedBytes / fileSize) * 100);
                    System.out.print("\r" + currPercent + "%");
                }
                if (buffPosition != 0) {
                    fos.write(Arrays.copyOfRange(fileBuff, 0, buffPosition));
                }
                fos.close();

                System.out.println("\r100%");

                endTime = System.currentTimeMillis();
                long resultTimeInSeconds = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.MILLISECONDS);
                double bandwidth = ((double) receivedBytes) / resultTimeInSeconds;
                System.out.println("File is downloaded. Total size: " + receivedBytes + " bytes.");
                System.out.println(String.format("Total time: %d s", resultTimeInSeconds));
                System.out.println(String.format("Bandwidth: %.3f MiB/s", (bandwidth / BYTES_PER_MEBIBYTE)));
            } catch (SocketTimeoutException e) {
                throw new CannotExecuteCommandException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("You're not connected to server.");
        }
    }
}
