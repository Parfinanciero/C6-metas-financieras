package com.riwi.goals.application.dtos.exception;

public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String message){
        super(message);
    }
}
