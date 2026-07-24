package br.com.fiap.commons.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@ResponseStatus(UNPROCESSABLE_CONTENT)
public class FieldNotFoundException extends RuntimeException {

    public FieldNotFoundException(Class<?> resourceClass, String fieldName) {
        super(format("Field [%s.%s] not found", resourceClass.getSimpleName(), fieldName));
    }
}
