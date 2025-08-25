package application.grid.domain.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String error;

    public ApiException(String message, HttpStatus httpStatus, String error) {
        super(message);
        this.httpStatus = httpStatus;
        this.error = error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getError() {
        return error;
    }
}
