package br.com.apigestao.domain.exceptions;

import br.com.apigestao.core.BaseException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException() {
        this("Not Found");
    }
}
