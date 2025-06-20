package br.com.apigestao.domain.exceptions;

import br.com.apigestao.core.BaseException;
import org.springframework.http.HttpStatus;

public class ServerException extends BaseException {
    public ServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ServerException() {
        this("Internal Error");
    }
}
