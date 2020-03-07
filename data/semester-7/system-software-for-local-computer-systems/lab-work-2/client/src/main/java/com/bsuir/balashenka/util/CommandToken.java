package com.bsuir.balashenka.util;

public class CommandToken {

    private String name;
    private String regex;
    private boolean required;

    public CommandToken(String name, String regex, boolean required) {
        this.name = name;
        this.regex = regex;
        this.required = required;
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