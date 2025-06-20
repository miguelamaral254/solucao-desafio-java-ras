package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_customers")
public class Customer extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @CPF
    @Column(unique = true, nullable = false)
    private String cpf;

    @Column()
    private String phone;

    @Email
    @Column(unique = true)
    private String email;

     /*
    “Apesar de não estar especificado nas
    regras, optei por adicionar uma restrição unique
    para o campo de e-mail.”
     */
}
