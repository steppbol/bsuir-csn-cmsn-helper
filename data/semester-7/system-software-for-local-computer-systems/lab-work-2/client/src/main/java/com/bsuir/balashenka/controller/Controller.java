package com.bsuir.balashenka.controller;

import com.bsuir.balashenka.service.Connection;

public interface Controller {
    void work();

    Connection getConnection();
}
