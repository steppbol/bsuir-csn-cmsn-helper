package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.service.Connection;

public interface ServerController {
    void work();

    Connection getConnection();
}
