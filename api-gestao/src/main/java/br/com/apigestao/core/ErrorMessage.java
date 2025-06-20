package br.com.apigestao.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorMessage {

    public static final String NO_MESSAGE_AVAILABLE = "No message available";

    private final String code;
    private final String message;

    public ErrorMessage() {
        this("NO_MESSAGE_AVAILABLE", NO_MESSAGE_AVAILABLE);
    }
}