package br.com.apigestao.core;

import java.time.LocalDateTime;

public interface BaseDTO {

    Long id();

    LocalDateTime createdDate();

    LocalDateTime lastModifiedDate();
}