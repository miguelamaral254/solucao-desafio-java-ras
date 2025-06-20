package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseDTO;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import br.com.apigestao.infrastructure.validations.UpdateValidation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

public record CustomerDTO(

        @Null(groups = CreateValidation.class)
        Long id,

        @NotBlank(groups = CreateValidation.class)
        String name,
        @CPF()
        @NotNull(groups = CreateValidation.class)
        String cpf,

        @NotBlank()
        String phone,

        @Email
        @NotBlank()
        String email,

        @Null(groups = CreateValidation.class)
        Boolean enabled,

        @Null(groups = CreateValidation.class)
        LocalDateTime createdDate,

        @Null(groups = CreateValidation.class)
        LocalDateTime lastModifiedDate

) implements BaseDTO {}