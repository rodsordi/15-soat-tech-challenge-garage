package br.com.fiap.commons.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@ResponseStatus(UNPROCESSABLE_CONTENT)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Class<?> resourceClass) {
        super(format("Resource [%s] not found", resourceClass.getSimpleName()));
    }

    public ResourceNotFoundException(Class<?> resourceClass, String fieldName, Object fieldValue) {
        super(format("Resource [%s] with [%s]: [%s] not found", resourceClass.getSimpleName(), fieldName, fieldValue));
    }
}
