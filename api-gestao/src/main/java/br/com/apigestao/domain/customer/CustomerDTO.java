package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseDTO;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

public record CustomerDTO (

        @Schema(description = "ID do cliente (não deve ser fornecido ao criar um novo cliente)", hidden = true)
        @Null(groups = CreateValidation.class, message = "O ID deve ser nulo")
        Long id,

        @Schema(description = "Nome completo do cliente", example = "João Silva")
        @NotBlank(groups = CreateValidation.class)
        @Pattern(regexp = "^[^0-9]*$", message = "Nome inválido. Não pode conter números.")
        String name,

        @Schema(description = "CPF do cliente", example = "21225491061")
        @NotNull(groups = CreateValidation.class)
        @CPF(message = "Formato de CPF inválido")
        String cpf,

        @Schema(description = "Telefone do cliente", example = "11999998888")
        @Nullable
        @Pattern(regexp = "^\\d{11}$", message = "Número de telefone inválido. Deve conter 11 dígitos sem separadores.")
        String phone,

        @Schema(description = "E-mail do cliente", example = "joao@email.com")
        @Nullable
        @Email(message = "Formato de e-mail inválido")
        String email,

        @Schema(description = "Status do cliente (não deve ser fornecido ao criar um novo cliente)", hidden = true)
        @Null(groups = CreateValidation.class, message = "O campo 'enabled' deve ser nulo")
        Boolean enabled,

        @Schema(description = "Data de criação do cliente (não deve ser fornecida ao criar um novo cliente)", hidden = true)
        @Null(groups = CreateValidation.class, message = "A data de criação deve ser nula")
        LocalDateTime createdDate,

        @Schema(description = "Data da última modificação do cliente (não deve ser fornecida ao criar um novo cliente)", hidden = true)
        @Null(groups = CreateValidation.class, message = "A data de última modificação deve ser nula")
        LocalDateTime lastModifiedDate
) implements BaseDTO {}