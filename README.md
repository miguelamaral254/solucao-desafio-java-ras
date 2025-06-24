# 🧪 Solução para o Desafio Técnico - Estágio / Desenvolvedor Júnior (Java + Spring)

Bem-vindo(a) à solução para o desafio técnico de seleção para estagiários e desenvolvedores júnior! Este repositório contém a implementação de uma API REST para o gerenciamento de **Clientes** e **Contas**, utilizando Java, Spring Boot, MapStruct, PostgreSQL e outras tecnologias. Abaixo estão os detalhes da implementação e a configuração do projeto.

---

## 🧩 Descrição do Projeto

O objetivo deste projeto é implementar uma API RESTful para gerenciar **Clientes** e suas **Contas**, conforme as especificações fornecidas.

### Requisitos da API:
- **Clientes**: Gerenciamento completo de clientes, com funcionalidades para criação, atualização, desativação e exclusão.
- **Contas**: Gerenciamento das contas de clientes, com criação, atualização, desabilitação e exclusão lógica.

### Tecnologias Utilizadas:

- **Java 17** 
- **Spring Boot 3.5.3**
- **Maven**
- **PostgreSQL** como banco de dados
- **MapStruct 1.6.3** para mapeamento entre entidades e DTOs
- **Swagger/OpenAPI** para documentação da API
- **JUnit 5** para testes automatizados
- **Lombok** para reduzir boilerplate de código
- **Spring Data JPA** para integração com o banco de dados
- **Log4j2** para logging
---

## 🧱 Estrutura das Entidades

### 📌 Cliente

| Campo           | Tipo          | Regras de Negócio                                  |
|-----------------|---------------|----------------------------------------------------|
| `id`            | Long          | Gerado automaticamente                             |
| `nome`          | String        | Obrigatório                                        |
| `cpf`           | String        | Obrigatório, único                                |
| `telefone`      | String        | Opcional                                           |
| `email`         | String        | Opcional                                           |
| `enabled`       | Boolean       | Indica se o cliente está ativo (soft delete)       |
| `createDate`    | LocalDateTime | Data de criação do cliente (auditoria)             |
| `updateDate`    | LocalDateTime | Data da última atualização do cliente (auditoria)  |

### 📌 Conta

| Campo           | Tipo          | Regras de Negócio                                  |
|-----------------|---------------|----------------------------------------------------|
| `id`            | Long          | Gerado automaticamente                             |
| `referencia`    | String        | Obrigatório. Formato: MM-AAAA                     |
| `valor`         | BigDecimal    | Obrigatório. Não pode ser menor que 0              |
| `situacao`      | Enum (String) | Obrigatório: PENDENTE, PAGA, CANCELADA             |
| `customer_id`   | Long          | Relacionamento com o cliente (obrigatório)         |
| `createDate`    | LocalDateTime | Data de criação da conta (auditoria)               |
| `updateDate`    | LocalDateTime | Data da última atualização da conta (auditoria)    |
#### Relacionamento: 
Uma conta está associada a um cliente.

### 📌 Regras de Negócio para Conta
- ❌ Não pode criar uma conta com valor menor que 0
- ❌ Não pode criar uma conta sem cliente associado
- ❌ Não pode criar uma conta já com a situação **CANCELADA**

### Foram criados os atributos enabled (para clientes), createDate e updateDate para auditoria dos registros inseridos.

---

## 📋 Funcionalidades obrigatórias (Endpoints)

### Clientes

- **[POST] /clientes**  
  👉 Cadastrar um novo cliente

- **[PUT] /clientes/{id}**  
  ✏️ Atualizar os dados de um cliente

- **[DELETE] /clientes/{id}**  
  🗑️ Excluir cliente (remoção permanente)

- **[GET] /clientes**  
  📃 Listar todos os clientes

### Contas

- **[POST] /clientes/{idCliente}/contas**  
  👉 Criar uma conta para um cliente

- **[PUT] /contas/{id}**  
  ✏️ Atualizar os dados de uma conta

- **[DELETE] /contas/{id}**  
  🚫 Excluir logicamente a conta (altera situação para CANCELADA)

- **[GET] /clientes/{idCliente}/contas**  
  📃 Listar todas as contas de um cliente

---

## 🔍 Exemplo de Requisições

### Criar Cliente

```http
POST /clientes
Content-Type: application/json

{
  "nome": "João Silva",
  "cpf": "12345678900",
  "telefone": "11999998888",
  "email": "joao@email.com"
}
```
Criar Conta
```http
POST /clientes/1/contas
Content-Type: application/json

{
  "referencia": "06-2025",
  "valor": 250.00,
  "situacao": "PENDENTE"
}
```

🛠️ Configuração do Projeto

1. Clone este repositório:

Clone este repositório utilizando o comando:

```bash
git clone https://github.com/miguelamaral254/solucao-desafio-java-ras.git
```

2. Configure o banco de dados PostgreSQL:

Configure o banco de dados PostgreSQL com as credenciais abaixo ou altere no arquivo application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/desafio
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

3. Execute o projeto:

Utilize o seguinte comando para iniciar a aplicação:

```bash
./mvnw spring-boot:run
```
4. Acesse o Swagger (Documentação):

A documentação interativa da API pode ser acessada em:

http://localhost:8080/swagger-ui/index.html

⸻

## 🚨 Observações

- Não foi implementada autenticação ou autorização, visto que o foco foi no gerenciamento de clientes e contas.
- O projeto foi entregue com todos os endpoints funcionando conforme o especificado.
- Foram implementados Testes unitários como diferencial.
⸻
## 📦 Extras

- **Swagger/OpenAPI**: A documentação da API foi gerada utilizando o Swagger.
- **DTOs**: Utilização de DTOs para a entrada e saída de dados (mapeamento com MapStruct).
- **Tratamento de Exceções**: Implementação do tratamento de exceções com `@RestControllerAdvice`.
- **Logging**: O projeto conta com logs configurados utilizando Log4j2.

---

## ✅ Critérios de Avaliação

- Funcionamento dos endpoints conforme especificado
- Implementação das regras de negócio (ex: não permitir criação de contas inválidas)
- Uso correto do Spring Web e Spring Data JPA
- Modelagem adequada das entidades e relacionamento entre elas
- Organização do projeto (pacotes, nomes de classes)
- Boas práticas de código (claridade, legibilidade)
- README bem estruturado (como este 😎)
- Testes (mínimo: unitário ou de integração simples)

---
