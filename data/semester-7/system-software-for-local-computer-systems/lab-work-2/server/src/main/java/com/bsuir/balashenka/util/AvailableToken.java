package com.bsuir.balashenka.util;

public enum AvailableToken {
    PATH("path", "^[\\w .-:\\\\]+$"),
    NAME("name", "^[\\w .-:\\\\]+$"),
    SESSION_KEY("session_key", "^[\\w .-:\\\\]+$"),
    CONTENT("content", null);

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
