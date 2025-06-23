package br.com.apigestao.domain.customer;

import br.com.apigestao.core.ApplicationResponse;
import br.com.apigestao.domain.account.*;
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
    private final AccountMapper accountMapper;
    private final AccountService accountService;

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
    @ApiResponse(responseCode = "409", description = "Conflito - Dados do cliente já existem (por exemplo, CPF ou e-mail já estão em uso)", content = {
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
            description = "Busca clientes utilizando filtros opcionais como e-mail, CPF e número de telefone. Retorna uma lista paginada de clientes."
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
                    "Os dados do cliente serão atualizados com os campos informados. Se os campos fornecidos contiverem dados inválidos, será retornado um erro 400. " +
                    "Se o cliente já existir (com base no CPF ou e-mail), será retornado um erro de conflito (409)."
    )
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"id\": 1, \"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}")
            )
    })
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para o cliente", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Formato do e-mail do cliente é inválido\"}")
            )
    })
    @ApiResponse(responseCode = "409", description = "Conflito - Os dados do cliente já existem (por exemplo, CPF ou e-mail já estão em uso)", content = {
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
        Customer customerUpdated = customerService.updateCustomer(id, Customer -> customerMapper.mergeNonNull(customerDtoUpdates, Customer));
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
    @ApiResponse(responseCode = "400", description = "ID de cliente inválido fornecido", content = {})
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

    @Operation(
            summary = "Criar uma nova conta",
            description = "Cria uma nova conta no sistema utilizando os dados fornecidos. " +
                    "A URI da conta criada será retornada no cabeçalho Location."
    )
    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"reference\": \"06-2025\", \"value\": 250.00, \"situation\": \"PENDENTE\" }")
            )
    })
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para a conta", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Formato da referência da conta é inválido\"}")
            )
    })
    @PostMapping("/{idCliente}/contas")
    public ResponseEntity<Void> createAccount(
            @PathVariable Long idCliente,
            @Validated(CreateValidation.class)
            @RequestBody AccountDTO accountDTO) {

        Account account = accountMapper.toEntity(accountDTO);
        Account savedAccount = accountService.createAccount(account, idCliente);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedAccount.getId())
                .toUri();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(location)
                .build();
    }

    @Operation(
            summary = "Listar todas as contas de um cliente",
            description = "Lista todas as contas associadas a um cliente com base no ID do cliente fornecido."
    )
    @ApiResponse(responseCode = "200", description = "Contas recuperadas com sucesso.", content = {})
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado.", content = {})
    @GetMapping("/{idCliente}/contas")
    public ResponseEntity<ApplicationResponse<Page<AccountDTO>>> getAccounts(
            @PathVariable Long idCliente,
            Pageable pageable) {

        Page<Account> accounts = accountService.findAccountsByCustomerId(idCliente, pageable);

        Page<AccountDTO> accountDTO = accountMapper.toDto(accounts);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(accountDTO));
    }
}
