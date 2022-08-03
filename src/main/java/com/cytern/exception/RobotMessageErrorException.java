package com.cytern.exception;

public class RobotMessageErrorException extends RuntimeException{
    public RobotMessageErrorException(String msg) {
        super(msg);
    }
}
