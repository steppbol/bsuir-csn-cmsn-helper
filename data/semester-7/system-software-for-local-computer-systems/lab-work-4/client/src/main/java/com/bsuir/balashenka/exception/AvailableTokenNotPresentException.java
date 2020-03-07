package com.bsuir.balashenka.exception;

public class AvailableTokenNotPresentException extends Exception {

    public AvailableTokenNotPresentException() {
    }

    public AvailableTokenNotPresentException(String message) {
        super(message);
    }

    public AvailableTokenNotPresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvailableTokenNotPresentException(Throwable cause) {
        super(cause);
    }

    public AvailableTokenNotPresentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
