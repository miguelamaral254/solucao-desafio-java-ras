package br.com.apigestao.domain.account;

import br.com.apigestao.domain.customer.Customer;
import br.com.apigestao.domain.customer.CustomerService;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import br.com.apigestao.domain.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.Consumer;

@AllArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Transactional
    public Account createAccount(Account account, Long idCliente) {
        Customer customer = customerService.findById(idCliente);
        account.setCustomer(customer);
        createValidation(account);
        log.info("Conta criada com sucesso");
        return accountRepository.save(account);
    }

    private void createValidation(Account account) {
        if (account.getSituation().equals(Situation.CANCELADA)) {
            log.error("Não é possível criar uma conta com esta situação");
            throw new InvalidException("Não é possível criar uma conta com esta situação");
        }
    }

    @Transactional(readOnly = true)
    public Page<Account> findAccountsByCustomerId(Long idCliente, Pageable pageable) {
        Customer customer = customerService.findById(idCliente);
        return accountRepository.findByCustomer(customer, pageable);
    }

    @Transactional(readOnly = true)
    public Account findById(Long id) {
        return accountRepository.findById(id).orElseThrow(()-> new NotFoundException("Conta não encontrada"));
    }

    @Transactional
    public Account updateAccount(Long id, Consumer<Account> mergeNonNull) {
        Account account = findById(id);
        Account updatedAccount = new Account();
        mergeNonNull.accept(updatedAccount);
        validateUpdate(account, updatedAccount);
        mergeNonNull.accept(account);
        Account updatedAccountInDb = accountRepository.save(account);
        log.info("Conta com ID: {} atualizada com sucesso [requestId={}]", account.getId(), MDC.get("requestId"));
        return updatedAccountInDb;
    }

    private void validateUpdate(Account account, Account updatedAccount) {
        if (account.getSituation().equals(Situation.CANCELADA)) {
            throw new UnauthorizedException("Contas canceladas não podem ser atualizadas");
        }
        if (updatedAccount.getValue() !=null && updatedAccount.getValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidException("O valor final após a atualização não pode ser negativo");
        }
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = findById(id);
        account.setSituation(Situation.CANCELADA);
        log.info("Conta com ID: {} foi deletada com sucesso [requestId={}]", id, MDC.get("requestId"));
        accountRepository.save(account);
    }
}