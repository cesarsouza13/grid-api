package application.grid.domain.exception;

public abstract class ApiException extends RuntimeException {

    public ApiException(String mensagem) {
        super(mensagem);
    }

}
