package com.sanidhy.quillcms.exception;

public class UnauthorisedActionException extends RuntimeException {
    public UnauthorisedActionException(String message) {
        super(message);
    }
}
