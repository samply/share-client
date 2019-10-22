package de.samply.share.client.util.connector.exception;

public class CTSConnectorException extends Exception {

    public CTSConnectorException(String message) {
        super(message);
    }

    public CTSConnectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CTSConnectorException(Throwable cause) {
        super(cause);
    }


}