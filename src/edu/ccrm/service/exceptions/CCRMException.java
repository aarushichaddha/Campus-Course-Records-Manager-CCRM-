package edu.ccrm.service.exceptions;

public class CCRMException extends Exception{
    public CCRMException(String message) {
        super(message);
    }

    public CCRMException(String message, Throwable cause) {
        super(message, cause);
    }
}
