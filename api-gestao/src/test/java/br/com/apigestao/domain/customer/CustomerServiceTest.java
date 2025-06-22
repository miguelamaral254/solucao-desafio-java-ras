package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.customer.factories.CustomerFactory;
import br.com.apigestao.domain.exceptions.ConflictException;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import jakarta.validation.ConstraintViolation;
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
import java.util.Set;
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
        Customer customer = CustomerFactory.savedCustomer();
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
    @DisplayName("Should throw InvalidException when the provided CPF is invalid")
    void createCustomer_whenCpfFormatIsInvalid_thenThrowInvalidException() {
        Customer customer = new Customer();
        customer.setCpf("123.456.789-00");
        customer.setName("Valid Name");

        Set<ConstraintViolation<Customer>> violations = Set.of(mock(ConstraintViolation.class));
        when(validator.validateProperty(any(Customer.class), anyString())).thenReturn(violations);

        InvalidException exception = assertThrows(InvalidException.class,
                () -> customerService.createCustomer(customer));

        assertEquals("Customer cpf is invalid", exception.getMessage());

        verify(customerRepository, never()).existsByCpf(anyString());
        verify(customerRepository, never()).save(any());
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

        // Mockando a resposta do repositório
        when(customerRepository.findAll(specification, pageable)).thenReturn(page);

        // Chamando o método que você quer testar
        Page<Customer> customerPage = customerService.searchCustomer(specification, pageable);

        // Verificando se o findAll foi chamado corretamente
        verify(customerRepository, times(1)).findAll(eq(specification), eq(pageable));

        // Verificando a resposta
        assertEquals(1, customerPage.getTotalElements());
        assertEquals(1, customerPage.getTotalPages());
        assertEquals("João Silva", customerPage.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should update customer successfully when valid fields are provided")
    void updateCustomer_whenFieldsAreValid_thenUpdateSuccessfully() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("João Silva");
        existingCustomer.setCpf("21225491061");
        String updatedCpf = "12345678901";

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByCpf(updatedCpf)).thenReturn(false);

        customerService.updateCustomer(customerId, customer -> {
            customer.setCpf(updatedCpf);
        });

        verify(customerRepository, times(1)).save(existingCustomer);
        verify(customerRepository, times(1)).existsByCpf(updatedCpf);

        assertEquals(updatedCpf, existingCustomer.getCpf());
        assertEquals("João Silva", existingCustomer.getName());
    }

    @Test
    @DisplayName("Should throw InvalidException when Email format is invalid")
    void updateCustomer_whenEmailIsInvalid_thenThrowInvalidException() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("João Silva");
        existingCustomer.setEmail("johndoe@example.com");
        String updatedEmail = "invalid-email";

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        InvalidException exception = assertThrows(InvalidException.class, () -> {
            customerService.updateCustomer(customerId, customer -> {
                customer.setEmail(updatedEmail);
            });
        });

        assertEquals("Customer email format is invalid", exception.getMessage());

        verify(customerRepository, never()).save(existingCustomer);
    }

    @Test
    @DisplayName("Should throw ConflictException when CPF already exists")
    void updateCustomer_whenCpfAlreadyExists_thenThrowConflictException() {
        Long customerId = 1L;
        Customer existingCustomer = new Customer();
        existingCustomer.setId(customerId);
        existingCustomer.setName("João Silva");
        existingCustomer.setCpf("21225491061");
        String updatedCpf = "98765432109";

        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setCpf(updatedCpf);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.existsByCpf(updatedCpf)).thenReturn(true);

        Exception exception = assertThrows(ConflictException.class, () -> {
            customerService.updateCustomer(customerId, customer -> {
                customer.setCpf(updatedCpf);
            });
        });

        assertEquals("Customer CPF already exists.", exception.getMessage());
        verify(customerRepository, never()).save(existingCustomer);
    }

    @Test
    @DisplayName("Should delete customer successfully when customer exists")
    void deleteCustomer_whenCustomerExists_thenDeleteSuccessfully() {
        long customerId = 1L;
        Customer customer = CustomerFactory.savedCustomer(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.deleteCustomer(customerId));

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    @DisplayName("Should throw NotFoundException when customer not found during delete")
    void deleteCustomer_whenCustomerNotFound_thenThrowNotFoundException() {
        long customerId = 1L;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> customerService.deleteCustomer(customerId));

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).delete(any(Customer.class));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should disable customer successfully when customer exists")
    void disableCustomer_whenCustomerExists_thenDisableSuccessfully() {
        long customerId = 1L;
        Customer customer = CustomerFactory.savedCustomer(customerId);
        customer.setEnabled(true);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> customerService.disableCustomer(customerId));

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(customer);

        assertFalse(customer.getEnabled());
    }

    @Test
    @DisplayName("Should throw InvalidException when customer is already disabled")
    void disableCustomer_whenCustomerAlreadyDisabled_thenThrowInvalidException() {
        long customerId = 1L;
        Customer customer = CustomerFactory.savedCustomer(customerId);
        customer.setEnabled(false);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        InvalidException exception = assertThrows(InvalidException.class,
                () -> customerService.disableCustomer(customerId));

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));

        assertEquals("Customer is already disabled", exception.getMessage());
    }
}
