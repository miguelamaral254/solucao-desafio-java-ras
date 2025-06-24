package br.com.apigestao.domain.account;

import br.com.apigestao.core.BaseMapper;
import br.com.apigestao.domain.customer.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper extends BaseMapper<Account, AccountDTO> {

    @Override
    @Mapping(source = "customer.id", target = "customerId")
    AccountDTO toDto(Account entity);

    @Override
    @Mapping(source = "customerId", target = "customer.id")
    Account toEntity(AccountDTO dto);

    @Override
    @Mapping(source = "customerId", target = "customer", qualifiedByName = "customerIdToCustomerEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void mergeNonNull(AccountDTO dto, @MappingTarget Account entity);

    @Named("customerIdToCustomerEntity")
    default Customer customerIdToCustomerEntity(Long customerId) {
        Customer customer = new Customer();
        customer.setId(customerId);
        return customer;
    }
}