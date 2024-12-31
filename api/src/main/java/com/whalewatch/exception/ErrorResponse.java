package com.whalewatch.exception;

public class ErrorResponse {
    private String errorMessage;
    private int statusCode;

    public ErrorResponse(String errorMessage, int statusCode) {
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
