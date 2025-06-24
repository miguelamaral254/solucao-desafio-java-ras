package br.com.apigestao.domain.account;

import br.com.apigestao.core.BaseEntity;
import br.com.apigestao.domain.customer.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_contas")
public class Account extends BaseEntity {

    @NotBlank
    @Column(name="referencia" ,nullable = false)
    private String reference;

    @NotNull
    @Column(name = "valor",nullable = false)
    @PositiveOrZero
    private BigDecimal value;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name ="situacao",nullable = false)
    private Situation situation;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
