package br.com.apigestao.domain.customer;

import br.com.apigestao.core.ApplicationResponse;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import br.com.apigestao.infrastructure.validations.UpdateValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

@Tag(name = "Clientes", description = "Operations related to managing customers in the system, including creating, updating, disabling, and deleting customer records.")
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerMapper customerMapper;
    private final CustomerService customerService;

    @Operation(
            summary = "Create a new customer",
            description = "Creates a new customer in the system using the provided customer data. " +
                    "The created customer's URI will be returned in the Location header. " +
                    "If the customer already exists (based on CPF or email), a conflict error will be returned."
    )
    @ApiResponse(responseCode = "201", description = "Customer successfully created", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "/1")
            )
    })
    @ApiResponse(responseCode = "400", description = "Invalid customer data provided", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Customer email format is invalid\"}")
            )
    })
    @ApiResponse(responseCode = "409", description = "Conflict - Customer data already exists (e.g., CPF or Email already in use)", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Customer email already exists\"}")
            )
    })
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

    @Operation(
            summary = "Update an existing Customer",
            description = "This operation updates an existing customer's details based on the provided customer ID. " +
                    "The customer's data will be updated with the provided fields. If the provided fields contain invalid data, a 400 error will be returned. If the customer already exists (based on CPF or email), a conflict error will be returned."
    )
    @ApiResponse(responseCode = "200", description = "Customer successfully updated", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}")
            )
    })
    @ApiResponse(responseCode = "400", description = "Invalid customer data provided", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Customer email format is invalid\"}")
            )
    })
    @ApiResponse(responseCode = "409", description = "Conflict - Customer data already exists (e.g., CPF or Email already in use)", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Customer email already exists\"}")
            )
    })
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
    @Operation(
            summary = "Delete a Customer by ID",
            description = "This operation deletes a customer from the system using the provided customer ID."
    )
    @ApiResponse(responseCode = "204", description = "Customer successfully deleted", content = {})
    @ApiResponse(responseCode = "404", description = "Customer not found", content = {})
    @ApiResponse(responseCode = "400", description = "Invalid customer ID provided", content = {})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Disable an existing Customer",
            description = "This operation disables an existing customer using the provided customer ID."
    )
    @ApiResponse(responseCode = "204", description = "Customer successfully disabled", content = {})
    @ApiResponse(responseCode = "404", description = "Customer not found", content = {})
    @ApiResponse(responseCode = "400", description = "Invalid customer ID or customer is already disabled", content = {})
    @PatchMapping("/{id}")
    public ResponseEntity<Void> disableCustomer(@PathVariable Long id) {
        customerService.disableCustomer(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
