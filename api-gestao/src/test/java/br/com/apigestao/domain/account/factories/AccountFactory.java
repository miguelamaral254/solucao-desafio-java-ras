package br.com.apigestao.domain.account.factories;

import br.com.apigestao.domain.account.Account;
import br.com.apigestao.domain.account.Situation;
import br.com.apigestao.domain.customer.Customer;
import br.com.apigestao.domain.customer.factories.CustomerFactory;

import java.math.BigDecimal;

public class AccountFactory {

    public static final Long DEFAULT_ID = 1L;
    public static final String DEFAULT_REFERENCE = "06-2025";
    public static final BigDecimal DEFAULT_VALUE = BigDecimal.valueOf(250.00);
    public static final Situation DEFAULT_SITUATION = Situation.PENDENTE;
    public static final Customer DEFAULT_CUSTOMER = CustomerFactory.validCustomer();

    private AccountFactory() {}

    private static Account baseAccount() {
        Account account = new Account();
        account.setReference(DEFAULT_REFERENCE);
        account.setValue(DEFAULT_VALUE);
        account.setSituation(DEFAULT_SITUATION);
        account.setCustomer(CustomerFactory.validCustomer());
        return account;
    }

    public static Account validAccount() {
        return baseAccount();
    }

    public static Account savedAccount(Long id, String reference, BigDecimal value, Situation situation, Customer customer) {
        Account account = baseAccount();
        account.setId(id);
        account.setReference(reference);
        account.setValue(value);
        account.setSituation(situation);
        account.setCustomer(customer);
        return account;
    }

    public static Account savedAccount(Long id) {
        return savedAccount(id, DEFAULT_REFERENCE, DEFAULT_VALUE, DEFAULT_SITUATION, DEFAULT_CUSTOMER);
    }

    public static Account savedAccount() {
        return savedAccount(DEFAULT_ID);
    }

    public static Account updatedAccount(Long id) {
        return savedAccount(id, "11/06", BigDecimal.valueOf(300.00), Situation.PAGA, CustomerFactory.updatedCustomer(3l));
    }

    public static Account invalidValueAccount() {
        Account account = baseAccount();
        account.setValue(BigDecimal.valueOf(-100.00));
        return account;
    }

    public static Account invalidReferenceAccount() {
        Account account = baseAccount();
        account.setReference("Invalid Reference");
        return account;
    }

    public static Account invalidSituationAccount() {
        Account account = baseAccount();
        account.setSituation(null);
        return account;
    }

    public static Account invalidCustomerAccount() {
        Account account = baseAccount();
        account.setCustomer(null);
        return account;
    }
}