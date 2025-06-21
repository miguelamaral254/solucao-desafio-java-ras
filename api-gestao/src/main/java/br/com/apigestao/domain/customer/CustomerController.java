package br.com.apigestao.domain.customer;

import br.com.apigestao.core.ApplicationResponse;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import br.com.apigestao.infrastructure.validations.UpdateValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Tag(name = "Clientes", description = "")
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerMapper customerMapper;
    private final CustomerService customerService;

    @Operation(
            summary = "Create a new customer",
            description = "Creates a new customer in the system using the provided customer data. The created customer's URI will be returned in the Location header."
    )
    @ApiResponse(responseCode = "201", description = "Customer successfully created", content = {})
    @ApiResponse(responseCode = "400", description = "Invalid customer data provided", content = {})
    @PostMapping
    public ResponseEntity<Void> createCustomer(
            @Validated(CreateValidation.class)
            @RequestBody CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntity(customerDTO);
        Customer savedCustomer = customerService.createCustomer(customer);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedCustomer.getId())
                .toUri();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .build();

    }

    @Operation(
            summary = "Search customers with filters and pagination",
            description = "Search for customers by optional filters like email, CPF, and phone number. Returns a paginated list of customers."
    )
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully", content = {})
    @ApiResponse(responseCode = "400", description = "Invalid filter data provided", content = {})
    @GetMapping
    public ResponseEntity<ApplicationResponse<Page<CustomerDTO>>> searchCustomers(
            @RequestParam(value="email", required = false) String email,
            @RequestParam(value="cpf", required = false) String cpf,
            @RequestParam(value="phone", required = false) String phone,
            Pageable pageable) {

        Specification<Customer> specification = (root, query, criteriaBuilder) -> null;

        if (email != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("email"), email));
        }

        if (cpf != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("cpf"), cpf));
        }

        if (phone != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("phone"), phone));
        }

        Page<Customer> customer = customerService.searchCustomer(specification, pageable);
        Page<CustomerDTO> customerDTO = customerMapper.toDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(customerDTO));
    }


    @Operation(summary = "Update an existing Customer")
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse<CustomerDTO>> updateCustomer(
            @PathVariable Long id,
            @Validated(UpdateValidation.class)
            @RequestBody CustomerDTO customerDtoUpdates) {
        Customer customerUpdated = customerService.updateCustomer(id, Customer -> customerMapper.mergeNonNull(customerDtoUpdates, Customer));
        CustomerDTO updatedCustomerDto = customerMapper.toDto(customerUpdated);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(updatedCustomerDto));
    }

    @Operation(summary = "Delete a Customer by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
