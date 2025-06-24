package br.com.apigestao.domain.account;

import br.com.apigestao.core.BaseDTO;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountDTO(
        @Schema(description = "O ID da conta não deve ser fornecido ao criar uma nova conta", hidden = true)
        @Null(groups = CreateValidation.class, message = "O ID deve ser nulo")
        Long id,

        @Schema(description = "Referência da conta", example = "06-2025")
        @Pattern(regexp = "^(0[1-9]|1[0-2])-[0-9]{4}$", message = "Formato inválido. Esperado MM-AAAA")
        @NotBlank(groups = CreateValidation.class, message = "Não pode ser nulo")
        String reference,

        @PositiveOrZero(groups = CreateValidation.class, message = "O valor da conta não pode ser negativo")
        @Schema(description = "Valor da conta", example = "250.00")
        @NotNull(groups = CreateValidation.class, message = "O valor da conta não pode ser nulo.")
        BigDecimal value,

        @Schema(description = "ID do cliente da conta", hidden = true)
        @Null(groups = CreateValidation.class)
        Long customerId,

        @Schema(description = "Situação da conta", example = "PENDENTE")
        @NotNull(groups = CreateValidation.class, message = "A situação da conta não pode ser nula")
        @Pattern(regexp = "PENDENTE|PAGA|CANCELADA", flags = Pattern.Flag.CASE_INSENSITIVE,
                message = "Situação inválida. Os valores válidos são: PENDENTE, PAGA, CANCELADA")
        String situation,

        @Schema(description = "Data de criação do cliente (não deve ser fornecida ao criar uma nova conta)", hidden = true)
        @Null(groups = CreateValidation.class, message = "A data de criação deve ser nula")
        LocalDateTime createdDate,

        @Schema(description = "Data da última modificação do cliente (não deve ser fornecida ao criar uma nova conta)", hidden = true)
        @Null(groups = CreateValidation.class, message = "A data de última modificação deve ser nula")
        LocalDateTime lastModifiedDate
) implements BaseDTO {}