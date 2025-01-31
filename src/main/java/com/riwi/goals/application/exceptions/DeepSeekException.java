package com.riwi.goals.application.exceptions;

import org.springframework.http.HttpStatus;

public class DeepSeekException extends RuntimeException{

    private final HttpStatus statusCode;

    public DeepSeekException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public class ApiLimitExceededException extends DeepSeekException {
        public ApiLimitExceededException() {
            super("Límite de API excedido", HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    public class InsufficientBalanceException extends DeepSeekException {
        public InsufficientBalanceException() {
            super("Saldo insuficiente", HttpStatus.PAYMENT_REQUIRED);
        }
    }
}
