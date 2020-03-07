package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.service.Connection;

public interface ClientController {
    void work();

    Connection getConnection();
}
