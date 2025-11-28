# PetWise Backend - API RESTful

Backend da plataforma PetWise desenvolvido em Spring Boot com Kotlin.

## Tecnologias Utilizadas

- **Spring Boot 3.5.6** - Framework principal
- **Kotlin 1.9.25** - Linguagem de programação
- **PostgreSQL** - Banco de dados
- **JWT** - Autenticação e autorização
- **Spring Security** - Segurança
- **Spring Data JPA** - Persistência
- **Spring Validation** - Validação de dados
- **Spring Cache + Caffeine** - Cache
- **Bucket4j** - Rate Limiting
- **JUnit 5 + MockK** - Testes unitários
- **JaCoCo** - Cobertura de testes
- **OpenAPI/Swagger** - Documentação da API

## Arquitetura

O projeto segue os princípios da **Arquitetura Limpa (Clean Architecture)**:

```
src/main/kotlin/edu/fatec/petwise/
├── application/          # Casos de uso e DTOs
│   ├── dto/             # Data Transfer Objects
│   ├── usecase/         # Casos de uso (lógica de negócio)
│   └── config/          # Configurações do Spring
├── domain/              # Regras de negócio
│   ├── entity/          # Entidades de domínio
│   ├── repository/      # Interfaces de repositório
│   ├── enums/           # Enums do domínio
│   └── valueobject/     # Value Objects
├── infrastructure/      # Camada de infraestrutura
│   ├── persistence/     # Repositórios JPA
│   ├── security/        # Configurações de segurança
│   └── config/          # Configurações técnicas
└── resources/           # Recursos estáticos e configurações
```

## Funcionalidades Principais

### Gestão de Pets
- ✅ Cadastro de pets com validação completa
- ✅ Atualização de informações
- ✅ Listagem com filtros por usuário
- ✅ Deleção segura (com validação de dados relacionados)
- ✅ Toggle de favoritos

### Gestão de Usuários
- ✅ Cadastro e autenticação (JWT)
- ✅ Perfis: OWNER, VETERINARY, PETSHOP
- ✅ Controle de permissões

### Gestão Veterinária
- ✅ Vacinas e imunizações
- ✅ Prescrições médicas
- ✅ Exames laboratoriais
- ✅ Consultas e agendamentos

### Segurança
- ✅ Autenticação JWT
- ✅ Rate limiting (Bucket4j)
- ✅ Validação de entrada
- ✅ CORS configurável

## Como Executar

### Pré-requisitos
- Java 21+
- Docker (para banco de dados)

### 1. Banco de Dados
```bash
docker compose up -d postgres
```

### 2. Aplicação
```bash
# Desenvolvimento
./gradlew bootRun

# Produção (com Docker)
docker compose up --build
```

### 3. Acessos
- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **Banco**: localhost:5432 (user: postgres, pass: admin)

## Testes

### Executar Todos os Testes
```bash
./gradlew test
```

### Cobertura de Testes
```bash
./gradlew jacocoTestReport
# Relatório: build/reports/jacoco/test/html/index.html
```

### Testes Específicos
```bash
# Apenas testes de unidade
./gradlew test --tests "*PetUseCaseTests*"

# Apenas testes de integração
./gradlew test --tests "*IntegrationTest*"
```

## Configuração

### Variáveis de Ambiente (.env)
```env
# Banco de dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petwise
DB_USERNAME=postgres
DB_PASSWORD=admin

# Servidor
SERVER_PORT=8080

# JWT
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000
JWT_RESET_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=60480000

# CORS
CORS_ALLOWED_ORIGINS=*

# Perfil Spring
SPRING_PROFILES_ACTIVE=dev
```

## Endpoints Principais

### Pets
- `GET /api/pets` - Listar pets
- `POST /api/pets` - Criar pet
- `PUT /api/pets/{id}` - Atualizar pet
- `DELETE /api/pets/{id}` - Deletar pet
- `PATCH /api/pets/{id}/favorite` - Toggle favorito

### Autenticação
- `POST /api/auth/login` - Login
- `POST /api/auth/register` - Registro
- `POST /api/auth/refresh` - Refresh token

### Veterinário
- `GET /api/vaccines` - Vacinas
- `GET /api/prescriptions` - Prescrições
- `GET /api/exams` - Exames

## Validações de Negócio

### Criação de Pet
- Nome obrigatório (não vazio)
- Raça obrigatória
- Espécie obrigatória (DOG, CAT, BIRD, RABBIT, OTHER)
- Gênero obrigatório (MALE, FEMALE)
- Status de saúde obrigatório
- Idade >= 0
- Peso > 0

### Deleção de Pet
- Apenas o dono pode deletar
- Pet não pode ter dados de veterinários (vacinas, prescrições, exames)

## Monitoramento

- **Actuator**: `/actuator/health`, `/actuator/info`, `/actuator/metrics`
- **Logs**: Configurados com SLF4J + Logback
- **Cache**: Métricas disponíveis via Actuator

## Desenvolvimento

### Estrutura de Commits
```
feat: nova funcionalidade
fix: correção de bug
docs: atualização de documentação
test: novos testes
refactor: refatoração de código
```

### Branches
- `main` - Produção
- `develop` - Desenvolvimento
- `feature/*` - Novas funcionalidades
- `bugfix/*` - Correções

## Contribuição

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'feat: adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

MIT
