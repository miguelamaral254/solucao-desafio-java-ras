package br.com.apigestao.domain.account;

import br.com.apigestao.core.ApplicationResponse;
import br.com.apigestao.infrastructure.validations.UpdateValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Contas", description = "Operações relacionadas ao gerenciamento de contas no sistema, incluindo atualização e desabilitação de registros de contas.")
@AllArgsConstructor
@RestController
@RequestMapping("/contas")
public class AccountController {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Operation(
            summary = "Desabilitar uma conta existente",
            description = "Esta operação desabilita uma conta existente utilizando o ID da conta fornecido."
    )
    @ApiResponse(responseCode = "204", description = "Conta desabilitada com sucesso", content = {})
    @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = {})
    @PatchMapping("/{id}")
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
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse<AccountDTO>> updateAccount(
            @PathVariable Long id,
            @Validated(UpdateValidation.class)
            @RequestBody AccountDTO accountDTOUpdates) {
        Account accountUpdated = accountService.updateAccount(id, Account -> accountMapper.mergeNonNull(accountDTOUpdates, Account));
        AccountDTO updatedAccountDto = accountMapper.toDto(accountUpdated);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApplicationResponse.ofSuccess(updatedAccountDto));
    }
}
