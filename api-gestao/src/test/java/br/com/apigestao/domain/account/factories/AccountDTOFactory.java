package br.com.apigestao.domain.account.factories;

import br.com.apigestao.domain.account.AccountDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDTOFactory {
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_REFERENCE = "06-2025";
    public static final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(250.00);
    public static final Long DEFAULT_CUSTOMER_ID = 1L;
    public static final String DEFAULT_SITUATION = "PENDENTE";

    private AccountDTOFactory() {}

    public static AccountDTO savedAccountDto(Long id, String reference, BigDecimal value, Long customerId, String situation) {
        LocalDateTime now = LocalDateTime.now();
        return new AccountDTO(
                id,
                reference,
                value,
                customerId,
                situation,
                now,
                now
        );
    }

    public static AccountDTO savedAccountDto(Long id) {
        return savedAccountDto(id, DEFAULT_REFERENCE, DEFAULT_VALUE, DEFAULT_CUSTOMER_ID, DEFAULT_SITUATION);
    }

    public static AccountDTO savedAccountDto(String reference) {
        return savedAccountDto(DEFAULT_ID, reference, DEFAULT_VALUE, DEFAULT_CUSTOMER_ID, DEFAULT_SITUATION);
    }

    public static AccountDTO savedAccountDto() {
        return savedAccountDto(DEFAULT_ID);
    }

    public static AccountDTO invalidAccountDto() {
        return new AccountDTO(
                null,
                "Invalid Reference",
                BigDecimal.valueOf(-100.00),
                null,
                "INVALID",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}