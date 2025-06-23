package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseDTO;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

public record CustomerDTO (

        @Schema(description = "Customer ID (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class, message = "Id must be null")
        Long id,

        @Schema(description = "Customer's full name", example = "João Silva")
        @NotBlank(groups = CreateValidation.class)
        @Pattern(regexp = "^[^0-9]*$", message = "Invalid name. It cannot contain numbers.")
        String name,

        @Schema(description = "Customer's CPF", example = "21225491061")
        @NotNull(groups = CreateValidation.class)
        @CPF(message = "Invalid CPF format")
        String cpf,

        @Schema(description = "Customer's phone number", example = "11999998888")
        @Nullable
        @Pattern(regexp = "^\\d{11}$", message = "Invalid phone number. It should consist of 11 digits without any separators.")
        String phone,

        @Schema(description = "Customer's email address", example = "joao@email.com")
        @Nullable
        @Email(message = "Invalid Email format")
        String email,

        @Schema(description = "Customer status (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class, message = "Enable must be null")
        Boolean enabled,

        @Schema(description = "Customer creation date (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class, message = "Create date must be null")
        LocalDateTime createdDate,

        @Schema(description = "Customer last modification date (should not be provided when creating a new customer)", hidden = true)
        @Null(groups = CreateValidation.class, message = "Last modified must be null")
         LocalDateTime lastModifiedDate
) implements BaseDTO{}