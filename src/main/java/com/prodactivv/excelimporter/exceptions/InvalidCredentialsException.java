package com.prodactivv.excelimporter.exceptions;

import java.util.function.Supplier;

public class InvalidCredentialsException extends Exception implements Supplier<InvalidCredentialsException> {

    public InvalidCredentialsException(String message) {
        super(message);
    }

    @Override
    public InvalidCredentialsException get() {
        return null;
    }
}
