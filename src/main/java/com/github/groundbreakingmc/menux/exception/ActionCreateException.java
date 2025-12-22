package com.github.groundbreakingmc.menux.exception;

public final class ActionCreateException extends RuntimeException {

    public ActionCreateException(String message) {
        super(message);
    }

    public ActionCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
