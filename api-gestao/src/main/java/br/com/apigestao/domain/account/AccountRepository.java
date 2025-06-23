package br.com.apigestao.domain.account;

import br.com.apigestao.domain.customer.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository extends JpaRepository<Account, Long> , JpaSpecificationExecutor<Account> {
    Page<Account> findByCustomer(Customer customer, Pageable pageable);

}
