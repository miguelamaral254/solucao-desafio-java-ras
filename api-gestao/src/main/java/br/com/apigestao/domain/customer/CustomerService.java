package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.exceptions.ConflictException;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final CustomerRepository customerRepository;
    private final Validator validator;
    private final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Transactional()
    public Customer createCustomer(Customer customer) {
        logger.info("Starting creation of customer");

        validateCreate(customer);

        logger.info("Saving customer...");
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer with ID: {} saved successfully", customer.getId());
        return savedCustomer;
    }

    private void validateCreate(Customer c) {
        if (c.getCpf() != null) {
            if (!isValidCpf(c.getCpf())) {
                logger.warn("Invalid CPF detected");
                throw new InvalidException("Customer cpf is invalid");
            }
            if (customerRepository.existsByCpf(c.getCpf())) {
                logger.warn("CPF already exists in the system");
                throw new ConflictException("Customer cpf already exists");
            }
        } else {
            logger.warn("CPF not provided for the customer");
            throw new InvalidException("Customer cpf is required");
        }
        if (c.getEmail() != null) {
            if (customerRepository.existsByEmail(c.getEmail())) {
                logger.warn("Email already exists in the system");
                throw new ConflictException("Customer email already exists");
            }
            if (!isValidEmail(c.getEmail())) {
                logger.warn("Invalid email format detected");
                throw new InvalidException("Customer email format is invalid");
            }
        }
        if (c.getName() == null || c.getName().trim().isEmpty()) {
            logger.warn("Customer name not provided");
            throw new InvalidException("Customer name is required");
        }
        if (c.getPhone() != null && !isValidPhoneNumber(c.getPhone())) {
            logger.warn("Invalid phone format detected");
            throw new InvalidException("Customer phone format is invalid. Use the format with 11 numbers: XXXXXXXXXXX.");
        }
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional(readOnly = true)
    public Page<Customer> searchCustomer(Specification<Customer> specification, Pageable pageable) {
        logger.info("Customer search query started. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return customerRepository.findAll(specification, pageable);
    }

    @Transactional
    public Customer updateCustomer(Long id, Consumer<Customer> mergeNonNull) {
        logger.info("Starting update of customer with ID: {}", id);
        Customer customer = findById(id);
        validateUpdate(customer, mergeNonNull);
        mergeNonNull.accept(customer);

        logger.info("Saving updated customer with ID: {}", customer.getId());
        Customer updatedCustomer = customerRepository.save(customer);
        logger.info("Customer with ID: {} updated successfully", customer.getId());

        return updatedCustomer;
    }

    private void validateUpdate(Customer existingCustomer, Consumer<Customer> mergeNonNull) {
        Customer newCustomer = new Customer();
        mergeNonNull.accept(newCustomer);

        if (newCustomer.getPhone() != null && !isValidPhoneNumber(newCustomer.getPhone())) {
            logger.debug("Invalid phone format detected");
            throw new InvalidException("Customer phone format is invalid. Use the format with 11 numbers: XXXXXXXXXXX.");
        }
        if (newCustomer.getName() != null && newCustomer.getName().trim().isEmpty()) {
            logger.warn("Customer name is empty");
            throw new InvalidException("Customer name is required.");
        }
        if (newCustomer.getEmail() != null && !newCustomer.getEmail().equals(existingCustomer.getEmail())) {
            if (!isValidEmail(newCustomer.getEmail())) {
                logger.debug("Invalid email format detected");
                throw new InvalidException("Customer email format is invalid");
            }
            if (customerRepository.existsByEmail(newCustomer.getEmail())) {
                logger.warn("Email already exists in the system");
                throw new ConflictException("Customer Email already exists.");
            }
        }

        if (newCustomer.getCpf() != null) {
            if (!newCustomer.getCpf().equals(existingCustomer.getCpf()) && customerRepository.existsByCpf(newCustomer.getCpf())) {
                logger.warn("CPF already exists in the system");
                throw new ConflictException("Customer CPF already exists.");
            }

            if (!isValidCpf(newCustomer.getCpf())) {
                logger.debug("Invalid CPF format detected for customer with ID: {}", existingCustomer.getId());
                throw new InvalidException("Customer CPF format is invalid");
            }
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        logger.info("Starting the delete process for customer with ID: {}", id);
        Customer c = findById(id);
        logger.info("Customer with ID: {} was successfully deleted", id);

        customerRepository.delete(c);
    }

    // Aqui inclui o disableCustomer() para soft delete
    @Transactional
    public void disableCustomer(Long id) {
        logger.info("Starting the disable process for customer with ID: {}", id);        Customer c = findById(id);
        validateDisable(c);
        c.setEnabled(false);
        logger.info("Customer with ID: {} was successfully disabled", id);
        customerRepository.save(c);
    }

    private void validateDisable(Customer c) {
        if (!c.getEnabled()) {
            logger.warn("Customer is already disabled in the system");
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
