package com.bsuir.balashenka.util;

import com.bsuir.balashenka.exception.AvailableTokenNotPresentException;

public enum AvailableToken {
    IP("ip", "^(\\d{1,3}\\.){3}\\d{1,3}$", true),
    HELP("help", null, false),
    PATH("path", "^[\\w .-:\\\\]+$", true),
    CONTENT("content", "^[\\w .-]+$", true),
    FORCE("force", null, false),
    NAME("name", "^[\\w .-:\\\\]+$", true);

    private String name;
    private String regex;
    private boolean required;

    AvailableToken(String name, String regex, boolean required) {
        this.name = name;
        this.regex = regex;
        this.required = required;
    }

    public static AvailableToken find(String name) throws AvailableTokenNotPresentException {
        for (AvailableToken t : values()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }

        throw new AvailableTokenNotPresentException("Token '" + name + "' is not available.");
    }

    public String getName() {
        return name;
    }

    public String getRegex() {
        return regex;
    }

    public boolean isRequired() {
        return required;
    }
}