package br.com.apigestao.domain.exceptions;

import br.com.apigestao.core.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        this("Unauthorized");
    }
}