package br.com.apigestao.domain.exceptions;

import br.com.apigestao.core.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidException extends BaseException {
    public InvalidException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public InvalidException() {
        this("Invalid");
    }
}
