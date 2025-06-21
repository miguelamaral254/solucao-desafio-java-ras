package br.com.apigestao.domain.customer;

import br.com.apigestao.core.ApplicationResponse;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import io.swagger.v3.oas.annotations.Operation;
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
    @GetMapping
    @Operation(summary = "Buscar clientes com filtros e paginação")
    public ResponseEntity<ApplicationResponse<Page<CustomerDTO>>> searchCustomers(
            @RequestParam(value="id", required = false) Long id,
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
}
