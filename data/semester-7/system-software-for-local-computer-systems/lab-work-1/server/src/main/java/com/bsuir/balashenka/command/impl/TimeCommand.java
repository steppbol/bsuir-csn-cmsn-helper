package com.bsuir.balashenka.command.impl;

import com.bsuir.balashenka.command.Command;
import com.bsuir.balashenka.controller.ServerController;
import com.bsuir.balashenka.controller.impl.TcpServerController;
import com.bsuir.balashenka.service.Connection;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCommand extends BaseCommand {
    private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private ServerController serverController;

    public TimeCommand() {
        serverController = TcpServerController.getInstance();
    }

    @Override
    public void execute() {
        Connection tcpConnection = serverController.getConnection();
        if (tcpConnection != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
                Date date = new Date();
                tcpConnection.write(dateFormat.format(date));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Command build() {
        return new TimeCommand();
    }
}
