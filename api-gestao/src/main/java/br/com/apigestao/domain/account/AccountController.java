package br.com.apigestao.domain.account;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@Tag(name = "Contas", description = "Operações relacionadas ao gerenciamento de contas, incluindo a atualização e " +
        "desabilitação de registros.")
@AllArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

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
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\":\"Cliente não encontrado\"}")
            )
    )
    @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos para a conta", content = {
            @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\": \"Formato inválido. Esperado MM-AAAA\"}")
            )
    })
    @PostMapping("/clientes/{idCliente}/contas")
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
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado.",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"message\":\"Cliente não encontrado\"}")
            )
    )@GetMapping("/clientes/{idCliente}/contas")
    public ResponseEntity<ApplicationResponse<Page<AccountDTO>>> getAccounts(
            @PathVariable Long idCliente,
            Pageable pageable) {

        Page<Account> accounts = accountService.findAccountsByCustomerId(idCliente, pageable);

        Page<AccountDTO> accountDTO = accountMapper.toDto(accounts);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(accountDTO));
    }

    @Operation(
            summary = "Desabilitar uma conta existente",
            description = "Esta operação desabilita uma conta existente utilizando o ID da conta fornecido."
    )
    @ApiResponse(responseCode = "204", description = "Conta desabilitada com sucesso", content = {})
    @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = {})
    @PatchMapping("/contas/{id}")
    public ResponseEntity<Void> disableAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Atualiza uma conta existente",
            description = "Esta operação atualiza uma conta existente utilizando os dados fornecidos."
    )
    @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso", content = {})
    @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = {})
    @ApiResponse(responseCode = "403", description = "Ação não autorizada", content = {})
    @PutMapping("/contas/{id}")
    public ResponseEntity<ApplicationResponse<AccountDTO>> updateAccount(
            @PathVariable Long id,
            @Validated(UpdateValidation.class)
            @RequestBody AccountDTO accountDTOUpdates) {
        Account accountUpdated = accountService.updateAccount(id, Account ->
                accountMapper.mergeNonNull(accountDTOUpdates, Account));
        AccountDTO updatedAccountDto = accountMapper.toDto(accountUpdated);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(updatedAccountDto));
    }

}
