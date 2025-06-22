package br.com.apigestao.domain.customer;

import br.com.apigestao.domain.customer.factories.CustomerDTOFactory;
import br.com.apigestao.domain.customer.factories.CustomerFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    CustomerMapper customerMapper = new CustomerMapperImpl();

    @Test
    @DisplayName("Should map Customer entity to CustomerDTO successfully")
    void toDto_whenEntityProvided_thenReturnDto() {
        Customer customer = CustomerFactory.savedCustomer();
        CustomerDTO customerDto = customerMapper.toDto(customer);

        assertCustomerEqualsCustomerDto(customer, customerDto);
    }

    @Test
    @DisplayName("Should map list of Customer entities to list of CustomerDTOs successfully")
    void toDtoList_whenEntityListProvided_thenReturnDtoList() {
        List<Customer> customerList = List.of(CustomerFactory.savedCustomer());
        List<CustomerDTO> customerDtoList = customerMapper.toDto(customerList);

        assertNotNull(customerDtoList);
        assertEquals(customerList.size(), customerDtoList.size());
        assertCustomerEqualsCustomerDto(customerList.get(0), customerDtoList.get(0));
    }

    @Test
    @DisplayName("Should map Customer Page to CustomerDTO Page successfully")
    void toDtoPage_whenEntityPageProvided_thenReturnDtoPage() {
        Page<Customer> customerPage = new PageImpl<>(List.of(CustomerFactory.savedCustomer()), Pageable.ofSize(1), 1L);
        Page<CustomerDTO> customerDtoPage = customerMapper.toDto(customerPage);

        assertNotNull(customerDtoPage);
        assertNotNull(customerDtoPage.getContent());
        assertEquals(customerPage.getSize(), customerDtoPage.getSize());
        assertEquals(customerPage.getTotalPages(), customerDtoPage.getTotalPages());
        assertEquals(customerPage.getTotalElements(), customerDtoPage.getTotalElements());
        assertEquals(customerPage.getNumber(), customerDtoPage.getNumber());
        assertEquals(customerPage.getNumberOfElements(), customerPage.getNumberOfElements());
        assertEquals(customerPage.getContent().size(), customerPage.getContent().size());
        assertCustomerEqualsCustomerDto(customerPage.getContent().get(0), customerDtoPage.getContent().get(0));
    }

    private void assertCustomerEqualsCustomerDto(Customer customer, CustomerDTO customerDto) {
        assertEquals(customer.getId(), customerDto.id());
        assertEquals(customer.getName(), customerDto.name());
        assertEquals(customer.getCpf(), customerDto.cpf());
        assertEquals(customer.getPhone(), customerDto.phone());
        assertEquals(customer.getEmail(), customerDto.email());
        assertEquals(customer.getEnabled(), customerDto.enabled());
        assertEquals(customer.getCreatedDate(), customerDto.createdDate());
        assertEquals(customer.getLastModifiedDate(), customerDto.lastModifiedDate());
    }

    @Test
    @DisplayName("Should map CustomerDTO to Customer entity successfully")
    void toEntity_whenDtoProvided_thenReturnEntity() {
        CustomerDTO customerDto = CustomerDTOFactory.savedCustomerDto();
        Customer customer = customerMapper.toEntity(customerDto);

        assertCustomerDtoEqualsCustomer(customerDto, customer);
    }

    @Test
    @DisplayName("Should map list of CustomerDTOs to list of Customer entities successfully")
    void toEntityList_whenDtoListProvided_thenReturnEntityList() {
        List<CustomerDTO> customerDtoList = List.of(CustomerDTOFactory.savedCustomerDto());
        List<Customer> customerList = customerMapper.toEntity(customerDtoList);

        assertNotNull(customerList);
        assertEquals(customerDtoList.size(), customerList.size());
        assertCustomerDtoEqualsCustomer(customerDtoList.get(0), customerList.get(0));
    }

    @Test
    @DisplayName("Should map CustomerDTO Page to Customer entity Page successfully")
    void toEntityPage_whenDtoPageProvided_thenReturnEntityPage() {
        Page<CustomerDTO> customerDtoPage = new PageImpl<>(List.of(CustomerDTOFactory.savedCustomerDto()), Pageable.ofSize(1), 1L);
        Page<Customer> customerPage = customerMapper.toEntity(customerDtoPage);

        assertNotNull(customerPage);
        assertNotNull(customerPage.getContent());
        assertEquals(customerDtoPage.getSize(), customerPage.getSize());
        assertEquals(customerDtoPage.getTotalPages(), customerPage.getTotalPages());
        assertEquals(customerDtoPage.getTotalElements(), customerPage.getTotalElements());
        assertEquals(customerDtoPage.getNumber(), customerPage.getNumber());
        assertEquals(customerDtoPage.getNumberOfElements(), customerPage.getNumberOfElements());
        assertEquals(customerDtoPage.getContent().size(), customerPage.getContent().size());
        assertCustomerDtoEqualsCustomer(customerDtoPage.getContent().get(0), customerPage.getContent().get(0));
    }

    private void assertCustomerDtoEqualsCustomer(CustomerDTO customerDto, Customer customer) {
        assertEquals(customerDto.id(), customer.getId());
        assertEquals(customerDto.name(), customer.getName());
        assertEquals(customerDto.cpf(), customer.getCpf());
        assertEquals(customerDto.phone(), customer.getPhone());
        assertEquals(customerDto.email(), customer.getEmail());
        assertEquals(customerDto.enabled(), customer.getEnabled());
        assertEquals(customerDto.createdDate(), customer.getCreatedDate());
        assertEquals(customerDto.lastModifiedDate(), customer.getLastModifiedDate());
    }
    @Test
    @DisplayName("Should merge CustomerDTO into Customer entity successfully")
    void mergeNonNull_whenDtoProvided_thenMergeDtoIntoEntity() {
        Customer customer = CustomerFactory.savedCustomer(1L);
        CustomerDTO customerDto = CustomerDTOFactory.savedCustomerDto(1L, "New Name");

        assertDoesNotThrow(() -> customerMapper.mergeNonNull(customerDto, customer));

        assertEquals(customerDto.name(), customer.getName());
    }
}