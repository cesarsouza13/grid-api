package application.grid.domain.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BadRequestException extends RuntimeException{

    private static final HttpStatus HTTP_STATUS = HttpStatus.BAD_REQUEST;
    private static final String ERROR = "Bad Request";

    public BadRequestException(String message) {
        super(message);
    }

    public HttpStatus getHttpStatus() {
        return HTTP_STATUS;
    }

    public String getError() {
        return ERROR;
    }
}
