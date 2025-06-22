package br.com.apigestao.domain.customer.factories;

import br.com.apigestao.domain.customer.CustomerDTO;
import java.time.LocalDateTime;

public class CustomerDTOFactory {
    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_NAME = "João Silva";
    public static final String DEFAULT_CPF = "21225491061";
    public static final String DEFAULT_PHONE = "11999998888";
    public static final String DEFAULT_EMAIL = "joao@email.com";

    private CustomerDTOFactory() {}

    // Método para criar CustomerDTO com parâmetros personalizados
    public static CustomerDTO savedCustomerDto(Long id, String name, String cpf, String phone, String email, Boolean enabled) {
        LocalDateTime now = LocalDateTime.now();
        return new CustomerDTO(
                id,
                name,
                cpf,
                phone,
                email,
                enabled,
                now,
                now
        );
    }

    public static CustomerDTO savedCustomerDto(Long id) {
        return savedCustomerDto(id, DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE, DEFAULT_EMAIL, true);
    }
    public static CustomerDTO savedCustomerDto(Long id, String name) {
        return savedCustomerDto(id, DEFAULT_NAME, DEFAULT_CPF, DEFAULT_PHONE, DEFAULT_EMAIL, true);
    }

    public static CustomerDTO savedCustomerDto(String name) {
        return savedCustomerDto(DEFAULT_ID, name, DEFAULT_CPF, DEFAULT_PHONE, DEFAULT_EMAIL, true);
    }

    public static CustomerDTO savedCustomerDto() {
        return savedCustomerDto(DEFAULT_ID);
    }

    // Método para criar CustomerDTO com dados inválidos
    public static CustomerDTO invalidCustomerDto() {
        return new CustomerDTO(
                null,
                "Invalid Name",
                "00000000000",
                "12345-6789",
                "invalid-email",
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}