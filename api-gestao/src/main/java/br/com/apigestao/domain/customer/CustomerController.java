package br.com.apigestao.domain.customer;

import br.com.apigestao.core.ApplicationResponse;
import br.com.apigestao.infrastructure.validations.CreateValidation;
import br.com.apigestao.infrastructure.validations.UpdateValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Clientes", description = "Operações relacionadas ao gerenciamento de clientes, incluindo a " +
        "criação, atualização, desativação e exclusão de registros.")
@AllArgsConstructor
@RequestMapping("/api/v1/clientes")
@RestController
public class CustomerController {
    private final CustomerMapper customerMapper;
    private final CustomerService customerService;

    @Operation(
            summary = "Criar um novo cliente",
            description = "Cria um novo cliente no sistema utilizando os dados fornecidos. " +
                    "A URI do cliente criado será retornada no cabeçalho Location. " +
                    "Se o cliente já existir (com base no CPF ou e-mail), será retornado um erro de conflito."
    )
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "/1")
            )
    })
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o cliente", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Formato de e-mail do cliente é inválido\"}")
            )
    })
    @ApiResponse(responseCode = "409", description = "Conflito - Dados do cliente já existem (por exemplo, CPF ou " +
            "e-mail já estão em uso)", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"E-mail do cliente já existe\"}")
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
            summary = "Buscar clientes com filtros e paginação",
            description = "Busca clientes utilizando filtros opcionais como e-mail, CPF e número de telefone. Retorna " +
                    "uma lista paginada de clientes."
    )
    @ApiResponse(responseCode = "200", description = "Clientes recuperados com sucesso", content = {})
    @ApiResponse(responseCode = "400", description = "Dados de filtro inválidos fornecidos", content = {})
    @GetMapping
    public ResponseEntity<ApplicationResponse<Page<CustomerDTO>>> searchCustomers(
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "enabled", required = false) Boolean enabled,
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

        if (enabled != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("enabled"), enabled));
        }

        Page<Customer> customer = customerService.searchCustomer(specification, pageable);
        Page<CustomerDTO> customerDTO = customerMapper.toDto(customer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(customerDTO));
    }

    @Operation(
            summary = "Atualizar um cliente existente",
            description = "Esta operação atualiza os dados de um cliente existente com base no ID fornecido. " +
                    "Os dados do cliente serão atualizados com os campos informados. Se os campos fornecidos contiverem" +
                    " dados inválidos, será retornado um erro 400. " +
                    "Se o cliente já existir (com base no CPF ou e-mail), será retornado um erro de conflito (409)."
    )
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"id\": 1, \"name\": \"John Doe\", \"email\":" +
                            " \"john.doe@example.com\"}")
            )
    })
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o cliente", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Formato do e-mail do cliente é inválido\"}")
            )
    })
    @ApiResponse(responseCode = "409", description = "Conflito - Os dados do cliente já existem (por exemplo, CPF ou " +
            "e-mail já estão em uso)", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"E-mail do cliente já existe\"}")
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse<CustomerDTO>> updateCustomer(
            @PathVariable Long id,
            @Validated(UpdateValidation.class)
            @RequestBody CustomerDTO customerDtoUpdates) {
        Customer customerUpdated = customerService.updateCustomer(id, Customer ->
                customerMapper.mergeNonNull(customerDtoUpdates, Customer));
        CustomerDTO updatedCustomerDto = customerMapper.toDto(customerUpdated);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(updatedCustomerDto));
    }

    @Operation(
            summary = "Deletar um cliente por ID",
            description = "Esta operação remove um cliente do sistema utilizando o ID fornecido."
    )
    @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso", content = {})
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = {})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Desabilitar um cliente existente",
            description = "Esta operação desabilita um cliente existente utilizando o ID fornecido."
    )
    @ApiResponse(responseCode = "204", description = "Cliente desabilitado com sucesso", content = {})
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = {})
    @PatchMapping("/{id}")
    public ResponseEntity<Void> disableCustomer(@PathVariable Long id) {
        customerService.disableCustomer(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
