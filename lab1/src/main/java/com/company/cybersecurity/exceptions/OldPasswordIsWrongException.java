package com.company.cybersecurity.exceptions;

public class OldPasswordIsWrongException extends Exception {
    public OldPasswordIsWrongException(String message) {
        super(message);
    }
}
