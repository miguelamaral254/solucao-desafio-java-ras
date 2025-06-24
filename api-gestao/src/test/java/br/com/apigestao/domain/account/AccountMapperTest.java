package br.com.apigestao.domain.account;

import br.com.apigestao.domain.account.factories.AccountDTOFactory;
import br.com.apigestao.domain.account.factories.AccountFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountMapperTest {

    AccountMapper accountMapper = new AccountMapperImpl();

    @Test
    @DisplayName("Should map Account entity to AccountDTO successfully")
    void toDto_whenEntityProvided_thenReturnDto() {
        Account account = AccountFactory.savedAccount();
        AccountDTO accountDto = accountMapper.toDto(account);

        assertAccountEqualsAccountDto(account, accountDto);
    }

    @Test
    @DisplayName("Should map list of Account entities to list of AccountDTOs successfully")
    void toDtoList_whenEntityListProvided_thenReturnDtoList() {
        List<Account> accountList = List.of(AccountFactory.savedAccount());
        List<AccountDTO> accountDtoList = accountMapper.toDto(accountList);

        assertNotNull(accountDtoList);
        assertEquals(accountList.size(), accountDtoList.size());
        assertAccountEqualsAccountDto(accountList.get(0), accountDtoList.get(0));
    }

    @Test
    @DisplayName("Should map Account Page to AccountDTO Page successfully")
    void toDtoPage_whenEntityPageProvided_thenReturnDtoPage() {
        Page<Account> accountPage = new PageImpl<>(List.of(AccountFactory.savedAccount()), Pageable.ofSize(1), 1L);
        Page<AccountDTO> accountDtoPage = accountMapper.toDto(accountPage);

        assertNotNull(accountDtoPage);
        assertNotNull(accountDtoPage.getContent());
        assertEquals(accountPage.getSize(), accountDtoPage.getSize());
        assertEquals(accountPage.getTotalPages(), accountDtoPage.getTotalPages());
        assertEquals(accountPage.getTotalElements(), accountDtoPage.getTotalElements());
        assertEquals(accountPage.getNumber(), accountDtoPage.getNumber());
        assertEquals(accountPage.getNumberOfElements(), accountDtoPage.getNumberOfElements());
        assertEquals(accountPage.getContent().size(), accountPage.getContent().size());
        assertAccountEqualsAccountDto(accountPage.getContent().get(0), accountDtoPage.getContent().get(0));
    }

    private void assertAccountEqualsAccountDto(Account account, AccountDTO accountDto) {
        assertEquals(account.getId(), accountDto.id());
        assertEquals(account.getReference(), accountDto.reference());
        assertEquals(account.getValue(), accountDto.value());
        assertEquals(account.getCustomer().getId(), accountDto.customerId());
        assertEquals(account.getSituation().name(), accountDto.situation());
        assertEquals(account.getCreatedDate(), accountDto.createdDate());
        assertEquals(account.getLastModifiedDate(), accountDto.lastModifiedDate());
    }

    @Test
    @DisplayName("Should map AccountDTO to Account entity successfully")
    void toEntity_whenDtoProvided_thenReturnEntity() {
        AccountDTO accountDto = AccountDTOFactory.savedAccountDto();
        Account account = accountMapper.toEntity(accountDto);

        assertAccountDtoEqualsAccount(accountDto, account);
    }

    @Test
    @DisplayName("Should map list of AccountDTOs to list of Account entities successfully")
    void toEntityList_whenDtoListProvided_thenReturnEntityList() {
        List<AccountDTO> accountDtoList = List.of(AccountDTOFactory.savedAccountDto());
        List<Account> accountList = accountMapper.toEntity(accountDtoList);

        assertNotNull(accountList);
        assertEquals(accountDtoList.size(), accountList.size());
        assertAccountDtoEqualsAccount(accountDtoList.get(0), accountList.get(0));
    }

    @Test
    @DisplayName("Should map AccountDTO Page to Account entity Page successfully")
    void toEntityPage_whenDtoPageProvided_thenReturnEntityPage() {
        Page<AccountDTO> accountDtoPage = new PageImpl<>(List.of(AccountDTOFactory.savedAccountDto()), Pageable.ofSize(1), 1L);
        Page<Account> accountPage = accountMapper.toEntity(accountDtoPage);

        assertNotNull(accountPage);
        assertNotNull(accountPage.getContent());
        assertEquals(accountDtoPage.getSize(), accountPage.getSize());
        assertEquals(accountDtoPage.getTotalPages(), accountPage.getTotalPages());
        assertEquals(accountDtoPage.getTotalElements(), accountPage.getTotalElements());
        assertEquals(accountDtoPage.getNumber(), accountPage.getNumber());
        assertEquals(accountDtoPage.getNumberOfElements(), accountPage.getNumberOfElements());
        assertEquals(accountDtoPage.getContent().size(), accountPage.getContent().size());
        assertAccountDtoEqualsAccount(accountDtoPage.getContent().get(0), accountPage.getContent().get(0));
    }

    private void assertAccountDtoEqualsAccount(AccountDTO accountDto, Account account) {
        assertEquals(accountDto.id(), account.getId());
        assertEquals(accountDto.reference(), account.getReference());
        assertEquals(accountDto.value(), account.getValue());
        assertEquals(accountDto.customerId(), account.getCustomer().getId());
        assertEquals(accountDto.situation(), account.getSituation().name());
        assertEquals(accountDto.createdDate(), account.getCreatedDate());
        assertEquals(accountDto.lastModifiedDate(), account.getLastModifiedDate());
    }

    @Test
    @DisplayName("Should merge AccountDTO into Account entity successfully")
    void mergeNonNull_whenDtoProvided_thenMergeDtoIntoEntity() {
        Account account = AccountFactory.savedAccount(1L);
        AccountDTO accountDto = AccountDTOFactory.savedAccountDto(1L, "11/2025", BigDecimal.valueOf(300.00), 2L, "PAGA");

        assertDoesNotThrow(() -> accountMapper.mergeNonNull(accountDto, account));

        assertEquals(accountDto.reference(), account.getReference());
        assertEquals(accountDto.value(), account.getValue());
        assertEquals(accountDto.customerId(), account.getCustomer().getId());
        assertEquals(accountDto.situation(), account.getSituation().name());
    }
}