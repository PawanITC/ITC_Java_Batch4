package com.itc.premiumsubscription.exception;

public class PlanAlreadyExistsException extends RuntimeException {
    public PlanAlreadyExistsException(String message) {
        super(message);
    }
}
