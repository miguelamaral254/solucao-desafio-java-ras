package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.exceptions.ConflictException;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
    private final CustomerRepository customerRepository;
    private final Logger log = LoggerFactory.getLogger(CustomerService.class);

    @Transactional()
    public Customer createCustomer(Customer customer) {
        validateCreate(customer);
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Cliente com ID: {} salvo com sucesso [requestId={}]", customer.getId(), MDC.get("requestId"));
        return savedCustomer;
    }

    private void validateCreate(Customer customer) {
        if (customerRepository.existsByCpf(customer.getCpf())) {
            log.error("CPF já existe no sistema [requestId={}]", MDC.get("requestId"));
            throw new ConflictException("O CPF do cliente já existe");
        }

        if (customerRepository.existsByEmail(customer.getEmail())) {
            log.error("Email já existe no sistema [requestId={}]", MDC.get("requestId"));
            throw new ConflictException("O email do cliente já existe");
        }
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> {
            log.error("Cliente não encontrado [requestId={}]", MDC.get("requestId"));
            return new NotFoundException("Cliente não encontrado");
        });
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
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Cliente com ID: {} atualizado com sucesso [requestId={}]", customer.getId(), MDC.get("requestId"));
        return updatedCustomer;
    }

    private void validateUpdate(Customer existingCustomer, Consumer<Customer> mergeNonNull) {
        Customer newCustomer = new Customer();
        mergeNonNull.accept(newCustomer);

        if (newCustomer.getName() != null && newCustomer.getName().trim().isEmpty()) {
            log.error("Nome do cliente está vazio [requestId={}]", MDC.get("requestId"));
            throw new InvalidException("O nome do cliente é obrigatório.");
        }
        if (newCustomer.getEmail() != null &&
                !newCustomer.getEmail().equals(existingCustomer.getEmail()) &&
                customerRepository.existsByEmail(newCustomer.getEmail())) {
            log.error("Email já existe no sistema [requestId={}]", MDC.get("requestId"));
            throw new ConflictException("O email do cliente já existe.");
        }

        if (newCustomer.getCpf() != null && (!newCustomer.getCpf().equals(existingCustomer.getCpf()) &&
                customerRepository.existsByCpf(newCustomer.getCpf()))) {
            log.error("CPF já existe no sistema [requestId={}]", MDC.get("requestId"));
            throw new ConflictException("O CPF do cliente já existe.");
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = findById(id);
        log.info("Cliente com ID: {} deletado com sucesso [requestId={}]", id, MDC.get("requestId"));
        customerRepository.delete(customer);
    }

    // Aqui inclui o disableCustomer() para soft delete
    @Transactional
    public void disableCustomer(Long id) {
        Customer customer = findById(id);
        validateDisable(customer);
        customer.setEnabled(false);
        log.info("Cliente com ID: {} foi desativado com sucesso [requestId={}]", id, MDC.get("requestId"));
        customerRepository.save(customer);
    }

    private void validateDisable(Customer c) {
        if (!c.getEnabled()) {
            log.error("Cliente já está desativado no sistema [requestId={}]", MDC.get("requestId"));
            throw new InvalidException("O cliente já está desativado");
        }
    }

}