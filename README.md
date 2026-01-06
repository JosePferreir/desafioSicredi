# Sistema de Gerenciamento de Votação

API REST desenvolvida para desafio tecnico Sicredi.

## Funcionalidades Principais

* **Gestão de Pautas:** Cadastro de assuntos para deliberação.
* **Gestão de Associados:** Cadastro de membros com validação de CPF único.
* **Sessões de Votação:** Abertura de votação com tempo de duração customizável.
* **Votação Agendada:** Suporte para definir data e hora de início no futuro.
* **Processamento de Resultados (Job):** Sistema de agendamento que encerra votações expiradas automaticamente e contabiliza os votos (Aprovado/Reprovado).

## Tecnologias e Ferramentas

* **Java 21** & **Spring Boot 3.4**
* **PostgreSQL:** Banco de dados relacional.
* **Docker & Docker Compose:** Containerização do ambiente de banco de dados local.
* **Flyway:** Gerenciamento de migrações e versão do esquema do banco.
* **SpringDoc OpenAPI (Swagger):** Documentação interativa da API.
* **JUnit 5 & Mockito:** Suíte de testes unitários e de resiliência.
* **JaCoCo:** Ferramenta de análise de cobertura de código.

## Como Executar

### Pré-requisitos
* Docker e Docker Compose instalados.
* JDK 21.

### 1. Subir o Banco de Dados
O projeto possui um arquivo `docker-compose.yml` pré-configurado. Na raiz do projeto, execute:
```bash
docker-compose up -d
```
### 2. Rodar a Aplicação
A aplicação está configurada para reconhecer o banco via perfil local. Use o Maven Wrapper:
```bash
./mvnw spring-boot:run
```
## Documentação da API (Swagger)

A documentação completa dos endpoints, incluindo modelos de requisição e códigos de resposta, pode ser acessada através da interface interativa do Swagger UI enquanto a aplicação estiver rodando:

🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Cobertura de Código (JaCoCo)

Para gerar o relatório de cobertura de testes, execute o comando:
```bash
./mvnw test
```
Após a execução, o relatório detalhado em HTML estará disponível em: 
**target/site/jacoco/index.html**
