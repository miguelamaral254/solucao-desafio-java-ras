package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends BaseMapper<Customer, CustomerDTO> { }
