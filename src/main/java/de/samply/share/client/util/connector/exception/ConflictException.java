package de.samply.share.client.util.connector.exception;

/**
 *
 *  ConflictException
 */
public class ConflictException extends Exception {
    public ConflictException(String message) {
        super(message);
    }
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConflictException(Throwable cause) {
        super(cause);
    }
}
