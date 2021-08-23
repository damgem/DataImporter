package com.damgem.dataImporter;

public class DataConnectorError extends Exception {
    String errorTitle, errorDescription;

    public DataConnectorError(String errorTitle, String errorDescription) {
        super(errorTitle + ": " + errorDescription);
        this.errorTitle = errorTitle;
        this.errorDescription = errorDescription;
    }
}