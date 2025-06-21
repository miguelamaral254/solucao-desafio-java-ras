package br.com.apigestao.infrastructure.config;

import br.com.apigestao.core.BaseException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        HttpStatus status = ex.getHttpStatus();

        return ResponseEntity.status(status)
                .body(new ErrorResponse(
                        String.valueOf(status.value()),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("500", "An unexpected error occurred."));
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}