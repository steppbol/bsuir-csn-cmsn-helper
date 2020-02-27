package com.bsuir.balashenka;

import com.bsuir.balashenka.controller.Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ClientApplication {
    public static void main(String[] args) {
        try (BufferedReader in = new BufferedReader(new FileReader("client\\src\\main\\resources\\banner.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Controller.getInstance().work();
    }
}