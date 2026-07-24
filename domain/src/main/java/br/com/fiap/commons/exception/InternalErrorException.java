package br.com.fiap.commons.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class InternalErrorException extends RuntimeException {

    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(Throwable e) {
        super(e);
    }

    public InternalErrorException(String message, Throwable e) {
        super(message, e);
    }
}
