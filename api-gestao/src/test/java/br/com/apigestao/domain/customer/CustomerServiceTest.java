package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.customer.factories.CustomerFactory;
import br.com.apigestao.domain.exceptions.ConflictException;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private Validator validator;

    @Test
    @DisplayName("Should create customer successfully when customer is valid")
    void createCustomer_whenCustomerIsValid_thenCreateSuccessfully() {
        Customer customer = CustomerFactory.validCustomer();
        Customer savedCustomer = CustomerFactory.savedCustomer();

        when(customerRepository.save(customer)).thenReturn(savedCustomer);
        when(customerRepository.existsByCpf(customer.getCpf())).thenReturn(false);
        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(false);

        Customer createdCustomer = customerService.createCustomer(customer);

        verify(customerRepository, times(1)).existsByCpf(customer.getCpf());
        verify(customerRepository, times(1)).existsByEmail(customer.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));

        assertNotNull(createdCustomer.getId());
        assertEquals(customer.getName(), createdCustomer.getName());
        assertEquals(customer.getCpf(), createdCustomer.getCpf());
        assertEquals(customer.getEmail(), createdCustomer.getEmail());
        assertEquals(customer.getPhone(), createdCustomer.getPhone());
    }

    @Test
    @DisplayName("Should throw ConflictException when CPF already exists")
    void createCustomer_whenCpfAlreadyExists_thenThrowConflictException() {
        Customer customer = CustomerFactory.savedCustomer();

        when(customerRepository.existsByCpf(customer.getCpf())).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> customerService.createCustomer(customer));

        verify(customerRepository, times(1)).existsByCpf(customer.getCpf());
        verify(customerRepository, never()).save(any(Customer.class));

        assertEquals("Customer cpf already exists", exception.getMessage());
    }

    @Test
    @DisplayName("Should return customer when found by id")
    void findCustomerById_whenCustomerExists_thenReturnCustomer() {
        long customerId = 1L;
        Customer customer = CustomerFactory.savedCustomer(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.findById(customerId);

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals(customerId, foundCustomer.getId());
        assertNotNull(foundCustomer.getName());
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer is not found")
    void findCustomerById_whenCustomerNotFound_thenThrowNotFoundException() {
        long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.findById(customerId));

        verify(customerRepository, times(1)).findById(customerId);

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return a page of customers when filters are provided")
    void searchCustomer_whenFiltersProvided_thenReturnPageOfCustomers() {
        String name = "João Silva";
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("João Silva");
        customer.setCpf("21225491061");
        customer.setEmail("joao@example.com");
        customer.setPhone("11999999999");

        Map<String, String> filters = Map.of("name", name);

        Specification<Customer> specification = (root, query, criteriaBuilder) -> {
            if (filters.containsKey("name")) {
                return criteriaBuilder.like(root.get("name"), "%" + filters.get("name") + "%");
            }
            return criteriaBuilder.conjunction();
        };

        Pageable pageable = Pageable.ofSize(1);
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1L);

        when(customerRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Customer> customerPage = customerService.searchCustomer(specification, pageable);

        verify(customerRepository, times(1)).findAll(eq(specification), eq(pageable));

        assertEquals(1, customerPage.getTotalElements());
        assertEquals(1, customerPage.getTotalPages());
        assertEquals("João Silva", customerPage.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should update customer successfully when valid fields are provided")
    void updateCustomer_whenFieldsAreValid_thenUpdateSuccessfully() {
        Customer existingCustomer = CustomerFactory.savedCustomer();
        String updatedEmail = "new@example.com";

        when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByEmail(updatedEmail)).thenReturn(false);

        customerService.updateCustomer(existingCustomer.getId(), customer -> {
            customer.setEmail(updatedEmail);
        });

        verify(customerRepository, times(1)).save(existingCustomer);
        verify(customerRepository, times(1)).existsByEmail(updatedEmail);

        assertEquals(updatedEmail, existingCustomer.getEmail());
        assertEquals("new@example.com", existingCustomer.getEmail());
    }

    @Test
    @DisplayName("Should throw ConflictException when CPF already exists")
    void updateCustomer_whenCpfAlreadyExists_thenThrowConflictException() {
        Customer existingCustomer = CustomerFactory.validCustomer();
        String updatedCpf = "98765432109";

        Customer anotherCustomer = CustomerFactory.validCustomer();
        anotherCustomer.setId(2L);
        anotherCustomer.setCpf(updatedCpf);

        when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByCpf(updatedCpf)).thenReturn(true);

        Exception exception = assertThrows(ConflictException.class, () -> {
            customerService.updateCustomer(existingCustomer.getId(), customer -> {
                customer.setCpf(updatedCpf);
            });
        });

        assertEquals("Customer CPF already exists.", exception.getMessage());
        verify(customerRepository, never()).save(existingCustomer);
    }

    @Test
    @DisplayName("Should delete customer successfully when customer exists")
    void deleteCustomer_whenCustomerExists_thenDeleteSuccessfully() {
        Customer customer = CustomerFactory.savedCustomer();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.deleteCustomer(customer.getId()));

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found during delete")
    void deleteCustomer_whenCustomerNotFound_thenThrowNotFoundException() {
        Customer customer = CustomerFactory.savedCustomer();

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.deleteCustomer(customer.getId()));

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, never()).delete(any(Customer.class));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should disable customer successfully when customer exists")
    void disableCustomer_whenCustomerExists_thenDisableSuccessfully() {
        Customer customer = CustomerFactory.savedCustomer();
        customer.setEnabled(true);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.disableCustomer(customer.getId()));

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, times(1)).save(customer);

        assertFalse(customer.getEnabled());
    }

    @Test
    @DisplayName("Should throw InvalidException when customer is already disabled")
    void disableCustomer_whenCustomerAlreadyDisabled_thenThrowInvalidException() {
        Customer customer = CustomerFactory.savedCustomer();
        customer.setEnabled(false);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        InvalidException exception = assertThrows(InvalidException.class,
                () -> customerService.disableCustomer(customer.getId()));

        verify(customerRepository, times(1)).findById(customer.getId());
        verify(customerRepository, never()).save(any(Customer.class));

        assertEquals("Customer is already disabled", exception.getMessage());
    }
}
