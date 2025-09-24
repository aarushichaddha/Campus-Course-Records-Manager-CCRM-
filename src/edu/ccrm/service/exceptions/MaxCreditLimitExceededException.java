package edu.ccrm.service.exceptions;

public class MaxCreditLimitExceededException extends CCRMException {
    public MaxCreditLimitExceededException(String message) {
        super(message);
    }
}
