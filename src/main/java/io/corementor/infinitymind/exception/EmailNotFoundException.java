package io.corementor.infinitymind.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The class Email Not Found Exception.
 * @author Blaise Mugisha
 * @version 1.0
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EmailNotFoundException extends Exception{
    /**
     * The constructor.
     * @param message String
     */
    public EmailNotFoundException(String message) {
        super(message);
    }
}
