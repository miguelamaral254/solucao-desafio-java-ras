package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseDTO;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDateTime;

public record CustomerDTO (

        @Schema(description = "Customer ID (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class, message = "Id must be null")
        Long id,

        @Schema(description = "Customer's full name", example = "Osvaldo")
        @NotBlank(groups = CreateValidation.class)
        String name,

        @Schema(description = "Customer's CPF", example = "12345678901")
        @NotNull(groups = CreateValidation.class)
        String cpf,

        @Schema(description = "Customer's phone number", example = "(11) 98765-4321")
        @Nullable
        String phone,

        @Schema(description = "Customer's email address", example = "osvaldo.uga@gmail.com")
        @Nullable
        String email,

        @Schema(description = "Customer status (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class)
        Boolean enabled,

        @Schema(description = "Customer creation date (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class)
        LocalDateTime createdDate,

        @Schema(description = "Customer last modification date (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class)
         LocalDateTime lastModifiedDate) implements BaseDTO{}