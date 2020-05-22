package com.lpp.transaction.exception;

/**
 * @author ：liupanpan
 */
public class ApplicationException extends RuntimeException {
    private static final long serialVersionUID = 8802360568403299200L;
    private final String errorCode;
    private final String errorMessage;

    public ApplicationException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
