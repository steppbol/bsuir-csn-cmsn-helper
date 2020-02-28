package com.bsuir.sdtt.controller;

import com.bsuir.sdtt.service.impl.DefaultGoogleDriveService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.bsuir.sdtt.service.impl.DefaultGoogleDriveService.SCOPES;

@RestController
@RequestMapping("api/v1/catalog/google")
public class GoogleController {
    private final DefaultGoogleDriveService connection;

    @Autowired
    public GoogleController(DefaultGoogleDriveService connection) {
        this.connection = connection;
    }

    @RequestMapping(value = "/oauth2callback", method = RequestMethod.GET)
    public void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        if (connection.exchangeCode(code)) {
            response.sendRedirect(connection.getSourceUrl());
        } else {
            response.sendRedirect("/error");
        }
    }

    @RequestMapping(value = "/ask", method = RequestMethod.GET)
    public void ask(HttpServletResponse response) throws IOException {

        String url = new GoogleAuthorizationCodeRequestUrl(connection.getClientSecrets(),
                connection.getRedirectUrl(), SCOPES)
                .setApprovalPrompt("force")
                .build();

        System.out.println("Go to the following link in your browser: ");
        System.out.println(url);

        response.sendRedirect(url);
    }
}