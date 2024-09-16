package org.example.fmuWindows.models;


public class FileValidationResult {
    private boolean isValid;
    private String message; // Use a more descriptive name like errorMessage

    public FileValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        return message;
    }
}
