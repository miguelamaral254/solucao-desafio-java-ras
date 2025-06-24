package br.com.apigestao.domain.customer;

import br.com.apigestao.core.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_clientes")
public class Customer extends BaseEntity {

    @NotBlank
    @Column(name = "nome", nullable = false)
    private String name;

    @NotBlank
    @CPF
    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;
    /*
       “Apesar de não estar especificado nas
       regras, optei por adicionar uma constraint unique
       para o campo de e-mail.”
    */
    @Email
    @Column(unique = true)
    private String email;

    @Column(name = "telefone")
    private String phone;

    //Tomei a liberdade de criar esse enabled para soft delete
    @Column(nullable = false)
    private Boolean enabled;

    @Override
    protected void onCreate() {
        super.onCreate();
        this.enabled = true;
    }
}
