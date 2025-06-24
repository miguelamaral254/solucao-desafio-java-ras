package br.com.apigestao.domain.account;

import br.com.apigestao.domain.customer.Customer;
import br.com.apigestao.domain.customer.CustomerService;
import br.com.apigestao.domain.customer.factories.CustomerFactory;
import br.com.apigestao.domain.account.factories.AccountFactory;
import br.com.apigestao.domain.exceptions.InvalidException;
import br.com.apigestao.domain.exceptions.NotFoundException;
import br.com.apigestao.domain.exceptions.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerService customerService;

    @Test
    @DisplayName("Should create account successfully when account and customer are valid")
    void createAccount_whenAccountIsValid_thenCreateSuccessfully() {
        Account account = AccountFactory.validAccount();
        Long customerId = 1L;
        Customer customer = CustomerFactory.savedCustomer(customerId);

        when(customerService.findById(customerId)).thenReturn(customer);
        when(accountRepository.save(account)).thenReturn(account);

        Account createdAccount = accountService.createAccount(account, customerId);

        verify(customerService, times(1)).findById(customerId);
        verify(accountRepository, times(1)).save(account);

        assertNotNull(createdAccount);
        assertEquals(account.getReference(), createdAccount.getReference());
        assertEquals(account.getValue(), createdAccount.getValue());
    }

    @Test
    @DisplayName("Should throw InvalidException when account situation is CANCELLED")
    void createAccount_whenAccountIsCancelled_shouldThrowException() {
        Account cancelledAccount = AccountFactory.savedAccount();
        cancelledAccount.setSituation(Situation.CANCELADA);
        Long customerId = 1L;

        when(customerService.findById(customerId)).thenReturn(CustomerFactory.savedCustomer(customerId));
        InvalidException exception = assertThrows(InvalidException.class, () -> {
            accountService.createAccount(cancelledAccount, customerId);
        });

        verify(accountRepository, never()).save(any());
        verify(customerService, times(1)).findById(customerId); // Verifica se foi chamado 1 vez

        assertEquals("Não é possível criar uma conta com esta situação", exception.getMessage());
    }

    @Test
    @DisplayName("Should return account when found by id")
    void findById_whenAccountExists_thenReturnAccount() {
        Long accountId = 1L;
        Account account = AccountFactory.savedAccount(accountId);

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(account));

        Account foundAccount = accountService.findById(accountId);

        verify(accountRepository, times(1)).findById(accountId);

        assertNotNull(foundAccount);
        assertEquals(accountId, foundAccount.getId());
    }

    @Test
    @DisplayName("Should throw NotFoundException when account is not found")
    void findById_whenAccountNotFound_thenThrowNotFoundException() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            accountService.findById(accountId);
        });

        assertEquals("Conta não encontrada", exception.getMessage());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    @DisplayName("Should return page of accounts when searching by customer")
    void findAccountsByCustomerId_whenAccountsExist_thenReturnPageOfAccounts() {
        Long customerId = 1L;
        Pageable pageable = Pageable.ofSize(1);
        Account account = AccountFactory.savedAccount(1L);
        Page<Account> page = new PageImpl<>(List.of(account), pageable, 1L);

        when(customerService.findById(customerId)).thenReturn(CustomerFactory.savedCustomer(customerId));
        when(accountRepository.findByCustomer(any(Customer.class), eq(pageable))).thenReturn(page);

        Page<Account> result = accountService.findAccountsByCustomerId(customerId, pageable);

        verify(accountRepository, times(1)).findByCustomer(any(Customer.class), eq(pageable));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(account.getReference(), result.getContent().get(0).getReference());
    }

    @Test
    @DisplayName("Should update account successfully when fields are valid")
    void updateAccount_whenFieldsAreValid_thenUpdateSuccessfully() {
        Long accountId = 1L;
        Account existingAccount = AccountFactory.savedAccount(accountId);
        String updatedReference = "11/2026";

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(existingAccount));
        when(accountRepository.save(existingAccount)).thenReturn(existingAccount);

        accountService.updateAccount(accountId, account -> account.setReference(updatedReference));

        verify(accountRepository, times(1)).save(existingAccount);

        assertEquals(updatedReference, existingAccount.getReference());
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when account is cancelled")
    void updateAccount_whenAccountIsCancelled_thenThrowUnauthorizedException() {
        Long accountId = 1L;
        Account existingAccount = AccountFactory.savedAccount(accountId);
        existingAccount.setSituation(Situation.CANCELADA);

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(existingAccount));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            accountService.updateAccount(accountId, account -> account.setReference("11/2026"));
        });

        assertEquals("Contas canceladas não podem ser atualizadas", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should throw InvalidException when updated account has negative value")
    void updateAccount_whenValueIsNegative_thenThrowInvalidException() {
        Long accountId = 1L;
        Account existingAccount = AccountFactory.savedAccount(accountId);

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(existingAccount));

        InvalidException exception = assertThrows(InvalidException.class, () -> {
            accountService.updateAccount(accountId, account -> account.setValue(BigDecimal.valueOf(-1)));
        });

        assertEquals("O valor final após a atualização não pode ser negativo", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    @DisplayName("Should delete account successfully when account exists")
    void deleteAccount_whenAccountExists_thenDeleteSuccessfully() {
        Long accountId = 1L;
        Account account = AccountFactory.savedAccount(accountId);

        when(accountRepository.findById(accountId)).thenReturn(java.util.Optional.of(account));

        assertDoesNotThrow(() -> accountService.deleteAccount(accountId));

        verify(accountRepository, times(1)).findById(accountId);
        verify(accountRepository, times(1)).save(account);
        assertEquals(Situation.CANCELADA, account.getSituation());
    }
}