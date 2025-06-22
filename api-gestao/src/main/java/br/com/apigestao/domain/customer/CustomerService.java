package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.exceptions.ConflictException;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.function.Consumer;

@AllArgsConstructor
@Service
public class CustomerService {
    private CustomerRepository customerRepository;
    private Validator validator;

    @Transactional()
    public Customer createCustomer(Customer customer) {
        validateCreate(customer);

        return customerRepository.save(customer);
    }

    private void validateCreate(Customer c) {
        if (c.getCpf() != null) {
            if (!isValidCpf(c.getCpf())) {
                throw new InvalidException("Customer cpf is invalid");
            }

            if (customerRepository.existsByCpf(c.getCpf())) {
                throw new ConflictException("Customer cpf already exists");
            }
        }
        if (c.getEmail() != null) {
            if (customerRepository.existsByEmail(c.getEmail())) {
                throw new ConflictException("Customer email already exists");
            }

            if (!isValidEmail(c.getEmail())) {
                throw new InvalidException("Customer email format is invalid");
            }
        }
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            throw new InvalidException("Customer name is required");
        }
        if (c.getPhone() != null && !isValidPhoneNumber(c.getPhone())) {
            throw new InvalidException("Customer phone format is invalid. Use the format with 11 numbers: XXXXXXXXXXX.");
        }
        if (c.getCpf() == null || c.getCpf().trim().isEmpty()) {
            throw new InvalidException("Customer cpf is required");
        }

    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public Page<Customer> searchCustomer(Specification<Customer> specification, Pageable pageable) {
        return customerRepository.findAll(specification, pageable);
    }

    @Transactional
    public Customer updateCustomer(Long id, Consumer<Customer> mergeNonNull) {
        Customer customer = findById(id);
        validateUpdate(customer, mergeNonNull);
        mergeNonNull.accept(customer);

        return customerRepository.save(customer);
    }

    private void validateUpdate(Customer existingCustomer, Consumer<Customer> mergeNonNull) {
        Customer newCustomer = new Customer();
        mergeNonNull.accept(newCustomer);

        if (newCustomer.getPhone() != null && !isValidPhoneNumber(newCustomer.getPhone())) {
            throw new InvalidException("Customer phone format is invalid. Use the format with 11 numbers: XXXXXXXXXXX.");
        }
        if (newCustomer.getName() != null && newCustomer.getName().trim().isEmpty()) {
            throw new InvalidException("Customer name is required.");
        }
        if (newCustomer.getEmail() != null && !newCustomer.getEmail().equals(existingCustomer.getEmail())) {
            if (!isValidEmail(newCustomer.getEmail())) {
                throw new InvalidException("Customer email format is invalid");
            }
            if (customerRepository.existsByEmail(newCustomer.getEmail())) {
                throw new ConflictException("Customer Email already exists.");
            }
        }

        if (newCustomer.getCpf() != null) {
            if (!newCustomer.getCpf().equals(existingCustomer.getCpf()) && customerRepository.existsByCpf(newCustomer.getCpf())) {
                throw new ConflictException("Customer CPF already exists.");
            }

            if (!isValidCpf(newCustomer.getCpf())) {
                throw new InvalidException("Customer cpf format is invalid");
            }
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer c = findById(id);
        customerRepository.delete(c);
    }

    // Aqui inclui o disableCustomer() para soft delete
    @Transactional
    public void disableCustomer(Long id) {
        Customer c = findById(id);
        validateDisable(c);
        c.setEnabled(false);
        customerRepository.save(c);
    }

    private void validateDisable(Customer c) {
        if (!c.getEnabled()) {
            throw new InvalidException("Customer is already disabled");
        }
    }
    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("^\\d{11}$");
    }
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    private boolean isValidCpf(String cpf) {
        Customer c = new Customer();
        c.setCpf(cpf);
        Set<ConstraintViolation<Customer>> violations = validator.validateProperty(c, "cpf");
        return violations.isEmpty();
    }
}
