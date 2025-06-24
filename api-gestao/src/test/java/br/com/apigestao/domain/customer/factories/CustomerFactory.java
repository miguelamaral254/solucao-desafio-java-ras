package br.com.apigestao.domain.customer.factories;

import br.com.apigestao.domain.customer.Customer;
import java.time.LocalDateTime;

public class CustomerFactory {

    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_NAME = "John Doe";
    public static final String DEFAULT_CPF = "21225491061";
    public static final String DEFAULT_EMAIL = "johndoe@example.com";
    public static final String DEFAULT_PHONE = "11987654321";
    public static final String UPDATED_NAME = "Updated Name";


    private CustomerFactory() {}

    private static Customer baseCustomer() {
        Customer customer = new Customer();
        customer.setId(DEFAULT_ID);
        customer.setName(DEFAULT_NAME);
        customer.setCpf(DEFAULT_CPF);
        customer.setEmail(DEFAULT_EMAIL);
        customer.setPhone(DEFAULT_PHONE);
        return customer;
    }


    public static Customer validCustomer() {
        return baseCustomer();
    }

    public static Customer savedCustomer(Long id, String name, String cpf, String email, String phone) {
        Customer customer = baseCustomer();
        customer.setId(id);
        customer.setName(name);
        customer.setCpf(cpf);
        customer.setEmail(email);
        customer.setPhone(phone);
        LocalDateTime now = LocalDateTime.now();
        customer.setCreatedDate(now);
        customer.setLastModifiedDate(now);
        return customer;
    }

    public static Customer savedCustomer(Long id) {
        return savedCustomer(id, DEFAULT_NAME, DEFAULT_CPF, DEFAULT_EMAIL, DEFAULT_PHONE);
    }

    public static Customer savedCustomer() {
        return savedCustomer(DEFAULT_ID);
    }
    public static Customer updatedCustomer(Long id) {
        return savedCustomer(id, UPDATED_NAME, DEFAULT_CPF, DEFAULT_EMAIL, DEFAULT_PHONE);
    }

    public static Customer invalidCpfCustomer() {
        Customer customer = baseCustomer();
        customer.setCpf("123");
        return customer;
    }

    public static Customer invalidEmailCustomer() {
        Customer customer = baseCustomer();
        customer.setEmail("invalid-email");
        return customer;
    }

    public static Customer invalidPhoneCustomer() {
        Customer customer = baseCustomer();
        customer.setPhone("12345");
        return customer;
    }
}