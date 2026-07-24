package br.com.fiap.commons.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.ResponseStatus;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@Getter
@ResponseStatus(UNPROCESSABLE_CONTENT)
public class AlreadyExistsException extends RuntimeException {

    private final Class<?> clazz;

    private final String id;

    public AlreadyExistsException(Class<?> clazz) {
        super(buildMessage(clazz, null, null));
        this.clazz = clazz;
        id = null;
    }

    public AlreadyExistsException(Class<?> clazz, String fieldName, String fieldValue) {
        super(buildMessage(clazz, fieldName, fieldValue));
        this.clazz = clazz;
        id = fieldValue;
    }

    private static String buildMessage(Class<?> clazz, String fieldName, String fieldValue) {
        var field = "";
        if (fieldName != null && fieldValue != null)
            field = format(" with %s: %s", fieldName, fieldValue);
        return format("%s already exists%s", clazz.getSimpleName(), field);
    }
}
