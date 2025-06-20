package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.exceptions.ConflictException;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import java.util.function.Consumer;

@AllArgsConstructor
@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    @Transactional()
    public Customer createCustomer(Customer customer) {
        validateBusinessRules(customer);

        return customerRepository.save(customer);
    }
    private void validateBusinessRules(Customer c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            throw new InvalidException("Customer name is required");
        }
        if (!c.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidException("Customer email format is invalid");
        }
        if (!c.getPhone().matches("\\d+")) {
            throw new InvalidException("Customer phone format is invalid. Only numbers are allowed.");
        }
        if (c.getCpf() == null || c.getCpf().trim().isEmpty()) {
            throw new InvalidException("Customer cpf is required");
        }
        if (customerRepository.existsByCpf(c.getCpf())) {
            throw new ConflictException("Customer cpf already exists");
        }
        if (customerRepository.existsByEmail(c.getEmail())) {
            throw new ConflictException("Customer email already exists");
        }
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Clente não encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<Customer> searchCustomer(Specification<Customer> specification, Pageable pageable) {
        return customerRepository.findAll(specification,pageable);
    }

    @Transactional
    public Customer updateCustomer(Long id, Consumer<Customer> mergeNonNull) {
        Customer customer = findById(id);
        final String oldEmail = customer.getEmail();
        final String oldCpf = customer.getCpf();
        final String oldPhone = customer.getPhone();
        final String oldName = customer.getName();

        mergeNonNull.accept(customer);
        validateUpdate(customer, oldEmail, oldCpf, oldPhone, oldName);

        return customerRepository.save(customer);
    }

    private void validateUpdate(Customer customer, String oldEmail, String oldCpf, String oldPhone, String oldName) {
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new InvalidException("Customer name is required");
        }
        if (!oldEmail.equals(customer.getEmail()) && customerRepository.existsByEmail(customer.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new InvalidException("Customer email is required");
        }
        if (!customer.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidException("Customer email format is invalid");
        }
        if (!oldPhone.equals(customer.getPhone()) && !customer.getPhone().matches("\\d+")) {
            throw new InvalidException("Customer phone format is invalid. Only numbers are allowed.");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new InvalidException("Customer phone is required");
        }
        if (!oldCpf.equals(customer.getCpf()) && customerRepository.existsByCpf(customer.getCpf())) {
            throw new ConflictException("CPF already exists");
        }
        if (customer.getCpf() == null || customer.getCpf().trim().isEmpty()) {
            throw new InvalidException("Customer cpf is required");
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }

    @Transactional
    public Customer disableCustomer(Long id, Boolean disable) {
        Customer c = findById(id);
        validateDisable(c, disable);
        c.setEnabled(disable);
        return customerRepository.save(c);
    }

    private void validateDisable(Customer c, Boolean disable) {
        if (!disable) {
            throw new InvalidException("Usuário já está desabilitado");
        }
        if (c.getEnabled().equals(disable)) {
            throw new InvalidException("Usuário já está desabilitado");
        }
    }
    // Incluido disableCustomer() para soft delete
}
