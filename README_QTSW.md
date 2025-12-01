# README_QTSW

## Visão Geral dos Testes

Este documento apresenta o resumo completo da suíte de testes unitários implementada para o projeto PetWise, focando no **Caso de Uso de Cadastro de Pet (UC-PET-01)**. Foram desenvolvidos **16 testes unitários** abrangendo:

- **Caminhos Felizes (Fluxo Principal)**
- **Fluxos Alternativos**
- **Fluxos de Exceção**
- **Regras de Negócio**
- **Grafo de Estados (GE) e Sequências Independentes**

### Framework de Testes Utilizado
- **JUnit 5** - Framework de testes
- **MockK** - Biblioteca de mocking para Kotlin
- **JaCoCo** - Cobertura de testes

### Estratégia de Testes
- **Testes Unitários** focados em casos de uso isolados
- **Mocks** para dependências externas (repositórios)
- **Stubs** para respostas pré-definidas
- **Fixtures** para dados de teste reutilizáveis
- **Nomenclatura** padronizada por sequência (SEQ1-01, SEQ2-01, etc.)

---

## Estrutura dos Arquivos de Teste

```
src/test/kotlin/edu/fatec/petwise/pets/
├── PetUseCaseTests.kt                    # Testes de Listagem de Pets (3 testes)
└── unit/
    └── CreatePetUseCaseTest.kt           # Testes de Cadastro com Grafo de Estados (13 testes)
```

**Total: 16 testes**

---

## Caso de Uso Testado: Cadastrar Pet (UC-PET-01)

### Arquivo Principal: `unit/CreatePetUseCaseTest.kt`

#### Descrição do Caso de Uso

| Campo | Descrição |
|-------|-----------|
| **Precondições** | O Usuário deve ter realizado o Cadastro e Login no sistema. |
| **Fluxo Principal** | 1) Usuário acessa "Adicionar Novo Pet" → 2) Sistema exibe formulário → 3) Usuário preenche dados → 4) Sistema valida → 5) Pet salvo → 6) Redirecionamento |
| **Fluxo Alternativo** | a) Campo opcional vazio b) Múltiplos pets |
| **Fluxo de Exceção** | Dados obrigatórios ausentes (nome, espécie, etc.) |
| **Pós-condições** | Pet registrado e associado ao Usuário |

#### Grafo de Estados (GE)

```
Estados:
  n1 = Início (usuário logado)
  n2 = Formulário de cadastro exibido
  n3 = Dados preenchidos pelo usuário
  n4 = Validação dos dados
  n5 = Pet salvo com sucesso
  n6 = Fim (redirecionamento para lista)
  n7 = Erro de validação (retorno ao formulário)
```

#### Quadro de Sequências Independentes

| Nº | Caminho Independente | Descrição |
|----|---------------------|-----------|
| 1 | n1 → n2 → n3 → n4 → n5 → n6 | Fluxo principal completo do cadastro de pet, sem ocorrência de erros. |
| 2 | n1 → n2 → n3 → n4 → n7 → n2 | Desvio por erro de validação. Sistema detecta dados inválidos e retorna ao formulário. |
| 3 | n1 → n2 → n3 → n4 → n7 → n2 → n3 → n4 → n5 → n6 | Erro seguido de correção. Testa o loop de retorno e continuação do fluxo. |

#### Regras de Negócio Relacionadas

| RN | Descrição |
|----|-----------|
| **RN01** | **Associação de Dono:** Um usuário (tutor) deve poder cadastrar e gerenciar vários pets. |
| **RN02** | **Dados Essenciais:** Cada pet deve ter obrigatoriamente um registro de espécie, nome e data de nascimento para ser cadastrado. |
| **RN03** | **Proteção de Histórico:** Não é permitido excluir um pet que já possua registros históricos. |

---

## Casos de Teste - CreatePetUseCase (13 testes)

### Sequência 1 - Fluxo Principal (n1 → n2 → n3 → n4 → n5 → n6)

| Teste | Descrição | Regra de Negócio |
|-------|-----------|------------------|
| **SEQ1-01** | Deve cadastrar pet com todos os campos obrigatórios | RN01, RN02 |
| **SEQ1-02** | Deve cadastrar pet sem histórico de saúde (campo opcional) | RN02 |
| **SEQ1-03** | Deve permitir cadastro de múltiplos pets (RN01) | RN01 |
| **SEQ1-04** | Deve aplicar trim nos campos de texto | RN02 |
| **SEQ1-05** | Deve aceitar espécie/gênero em lowercase | RN02 |

### Sequência 2 - Erro de Validação (n1 → n2 → n3 → n4 → n7 → n2)

| Teste | Descrição | Regra de Negócio |
|-------|-----------|------------------|
| **SEQ2-01** | Deve rejeitar nome vazio | RN02 |
| **SEQ2-02** | Deve rejeitar nome apenas com espaços | RN02 |
| **SEQ2-03** | Deve rejeitar espécie inválida | RN02 |
| **SEQ2-04** | Deve rejeitar espécie vazia | RN02 |
| **SEQ2-05** | Deve rejeitar raça vazia | RN02 |
| **SEQ2-06** | Deve rejeitar idade negativa | RN02 |
| **SEQ2-07** | Deve rejeitar peso zero ou negativo | RN02 |

### Sequência 3 - Erro + Correção

| Teste | Descrição | Regra de Negócio |
|-------|-----------|------------------|
| **SEQ3-01** | Deve permitir correção após erro de validação | RN01, RN02 |

---

## Casos de Teste - GetAllPetsUseCase (3 testes)

### Arquivo: `PetUseCaseTests.kt`

Testes de listagem de pets cadastrados (complementar ao cadastro):

| Teste | Tipo | Descrição | Regra de Negócio |
|-------|------|-----------|------------------|
| **T01** | ✅ Happy Path | OWNER deve ver apenas seus pets cadastrados | RN01, RN10 |
| **T02** | 🔄 Alternativo | VETERINARY deve ver todos os pets | RN11 |
| **T03** | ❌ Exceção | Lista vazia para usuário inexistente | - |

---

## Detalhamento dos Testes

### SEQ1-01 - Happy Path: Cadastro completo
```kotlin
@Test
@DisplayName("SEQ1-01: Deve cadastrar pet com todos os campos obrigatórios")
fun `deve cadastrar pet com todos campos obrigatorios`() {
    // Arrange - Estado n3: dados preenchidos
    val request = createValidRequest()
    val savedPet = createPet()
    
    // STUB: Simula estado n5 (pet salvo)
    every { petRepository.save(any()) } returns savedPet
    
    // Act - Transição n3 → n4 → n5 → n6
    val result = createPetUseCase.execute(testUserId, request)
    
    // Assert - Verificar estado n6 (sucesso)
    Assertions.assertNotNull(result)
    assertEquals("Rex", result.name)
    assertEquals("DOG", result.species)
    assertEquals(testUserId, result.ownerId) // RN01: Vinculado ao usuário
    
    // Verificar que o repositório foi chamado
    verify(exactly = 1) { petRepository.save(any()) }
}
```

### SEQ2-01 - Exceção: Nome vazio
```kotlin
@Test
@DisplayName("SEQ2-01: Deve rejeitar nome vazio (RN02)")
fun `deve rejeitar nome vazio`() {
    // Arrange - Estado n3: nome vazio (dado inválido)
    val request = createValidRequest(name = "")
    
    // Act & Assert - Transição n4 → n7 (erro)
    val exception = assertThrows<IllegalArgumentException> {
        createPetUseCase.execute(testUserId, request)
    }
    
    assertEquals("Nome do pet é obrigatório", exception.message)
    
    // Verificar que NÃO atingiu estado n5
    verify(exactly = 0) { petRepository.save(any()) }
}
```

### SEQ3-01 - Erro + Correção
```kotlin
@Test
@DisplayName("SEQ3-01: Deve permitir correção após erro de validação")
fun `deve permitir correcao apos erro de validacao`() {
    // ===== PRIMEIRA TENTATIVA (falha) =====
    val requestInvalido = createValidRequest(name = "")
    assertThrows<IllegalArgumentException> {
        createPetUseCase.execute(testUserId, requestInvalido)
    }
    verify(exactly = 0) { petRepository.save(any()) }
    
    // ===== SEGUNDA TENTATIVA (sucesso) =====
    val requestValido = createValidRequest(name = "Rex")
    val savedPet = createPet(name = "Rex")
    every { petRepository.save(any()) } returns savedPet
    
    val result = createPetUseCase.execute(testUserId, requestValido)
    
    Assertions.assertNotNull(result)
    assertEquals("Rex", result.name)
    verify(exactly = 1) { petRepository.save(any()) }
}
```

---

## Cobertura de Cenários

### ✅ Caminhos Felizes (Happy Path)
- Cadastro com todos os campos (SEQ1-01)
- Cadastro com campo opcional vazio (SEQ1-02)
- Múltiplos pets do mesmo usuário (SEQ1-03)
- Listagem de pets do OWNER (T01)

### 🔄 Fluxos Alternativos
- Trim em campos de texto (SEQ1-04)
- Enums em lowercase (SEQ1-05)
- Listagem por VETERINARY (T02)

### ❌ Fluxos de Exceção
- Nome vazio/espaços (SEQ2-01, SEQ2-02)
- Espécie inválida/vazia (SEQ2-03, SEQ2-04)
- Raça vazia (SEQ2-05)
- Idade negativa (SEQ2-06)
- Peso zero/negativo (SEQ2-07)
- Correção após erro (SEQ3-01)
- Usuário inexistente (T03)

---

## Como Executar os Testes

### Pré-requisitos
- **Java 21** ou superior instalado
- **Gradle 8.x** (wrapper incluído no projeto)
- Terminal com acesso à pasta do projeto

### Acessar o Diretório do Projeto
```bash
cd PI_VI_Back-end
```

### 1. Executar Todos os Testes
```bash
# Windows (PowerShell)
./gradlew.bat test

# Linux/Mac
./gradlew test
```

### 2. Executar Apenas Testes de Pet
```bash
# Windows (PowerShell)
./gradlew.bat testPet

# Linux/Mac
./gradlew testPet
```

### 3. Executar Testes Específicos por Arquivo
```bash
# Testes de criação de pet
./gradlew.bat test --tests "*CreatePetUseCaseTest*"
```

### 4. Executar Testes por Pacote
```bash
# Todos os testes unitários de Pet
./gradlew.bat test --tests "edu.fatec.petwise.pets.unit.*"

```

---

## Gerar Relatórios de Teste

### 1. Relatório HTML de Testes (JUnit)
Os relatórios são gerados automaticamente após a execução dos testes.

```bash
# Executar testes (gera relatório automaticamente)
./gradlew.bat test
```

**Localização do Relatório:**
```
build/reports/tests/test/index.html
```

### 2. Relatório de Cobertura JaCoCo

#### 2.1 Cobertura Geral (Todos os Testes)
```bash
# Windows (PowerShell)
./gradlew.bat testWithReport

# Linux/Mac
./gradlew testWithReport
```

**Localização do Relatório:**
```
build/reports/jacoco/html/index.html
```

#### 2.2 Cobertura Específica para Pet
```bash
# Windows (PowerShell)
./gradlew.bat testPetWithReport

# Linux/Mac
./gradlew testPetWithReport
```

**Localização do Relatório:**
```
build/reports/jacoco/pet/index.html
```

### 3. Verificar Cobertura Mínima
```bash
./gradlew.bat jacocoTestCoverageVerification
```

Este comando falha se a cobertura estiver abaixo de:
- **60%** de cobertura geral
- **70%** de cobertura de linhas nos Use Cases

---

## Estrutura dos Relatórios

### Relatórios Gerados
```
build/
├── reports/
│   ├── tests/
│   │   ├── test/
│   │   │   └── index.html          # Relatório JUnit (todos os testes)
│   │   └── pet/
│   │       └── index.html          # Relatório JUnit (apenas Pet)
│   └── jacoco/
│       ├── html/
│       │   └── index.html          # Cobertura geral
│       └── pet/
│           └── index.html          # Cobertura Pet
├── test-results/
│   ├── test/                       # Resultados XML (todos)
│   └── pet/                        # Resultados XML (Pet)
└── jacoco/
    ├── test.exec                   # Dados de execução (todos)
    └── testPet.exec                # Dados de execução (Pet)
```

### Estrutura dos Arquivos de Teste
```
src/test/kotlin/edu/fatec/petwise/pets/
├── PetUseCaseTests.kt                    # Testes de Listagem de Pets (3 testes)
└── unit/
    └── CreatePetUseCaseTest.kt           # Testes CreatePet com Grafo de Estados (13 testes)
```

**Organização por Caso de Uso:**
- `CreatePetUseCaseTest.kt` - 13 testes (Sequências 1, 2 e 3 do GE)
- `PetUseCaseTests.kt` - 3 testes (T01 a T03 - Listagem de Pets)

**Total: 16 testes unitários**

### Como Abrir os Relatórios

#### Windows (PowerShell)
```powershell
# Abrir relatório de testes
Start-Process "build\reports\tests\test\index.html"

# Abrir relatório de cobertura
Start-Process "build\reports\jacoco\html\index.html"

# Abrir relatório de cobertura Pet
Start-Process "build\reports\jacoco\pet\index.html"
```

#### Linux/Mac
```bash
# Abrir relatório de testes
open build/reports/tests/test/index.html

# Abrir relatório de cobertura
open build/reports/jacoco/html/index.html
```

---

## Comandos Rápidos

| Comando | Descrição |
|---------|-----------|
| `./gradlew.bat test` | Executa todos os testes |
| `./gradlew.bat testPet` | Executa apenas testes de Pet |
| `./gradlew.bat testWithReport` | Testes + Relatório de cobertura |
| `./gradlew.bat testPetWithReport` | Testes Pet + Relatório de cobertura |
| `./gradlew.bat jacocoTestReport` | Gera apenas relatório de cobertura |
| `./gradlew.bat jacocoTestCoverageVerification` | Verifica cobertura mínima |
| `./gradlew.bat clean test` | Limpa cache e executa testes |

---

## Resumo Final

| Arquivo | Casos de Teste | Descrição |
|---------|----------------|-----------|
| `CreatePetUseCaseTest.kt` | 13 | Testes seguindo Grafo de Estados (GE) - Cadastro |
| `PetUseCaseTests.kt` | 3 | Testes de Listagem de Pets (GetAllPets) |
| **Total** | **16** | Cobertura de cadastro e listagem de Pet |

### Legenda de Tipos de Teste
- ✅ **Happy Path**: Fluxo principal bem-sucedido
- 🔄 **Alternativo**: Fluxos alternativos válidos
- ❌ **Exceção**: Tratamento de erros e validações

---

## Troubleshooting

### Erro: "Permission denied"
```bash
# Linux/Mac - dar permissão de execução
chmod +x gradlew
```

### Erro: "JAVA_HOME not set"
```bash
# Verificar se Java está instalado
java -version

# Definir JAVA_HOME (Windows PowerShell)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

### Erro: "Connection refused" (Testes de Integração)
Os testes de integração requerem banco H2 em memória. Verifique se o perfil `test` está configurado em `application-test.yml`.

### Limpar Cache de Testes
```bash
./gradlew.bat clean
./gradlew.bat test
```

---

## Estratégia de Mocks e Padrões de Teste

### Padrões Utilizados

| Padrão | Descrição | Exemplo |
|--------|-----------|---------|
| **MOCK** | Simula dependências externas | `@MockK lateinit var petRepository: PetRepository` |
| **STUB** | Define retornos pré-definidos | `every { petRepository.save(any()) } returns savedPet` |
| **FIXTURE** | Dados de teste reutilizáveis | `fun createValidRequest()` e `fun createPet()` |
| **DRIVER** | Componente que invoca o SUT | Classe de teste que chama `useCase.execute()` |

### Repositórios Mockados
```kotlin
@MockK private lateinit var petRepository: PetRepository
@MockK private lateinit var userRepository: UserRepository
@MockK private lateinit var iotService: IotService  // Mockado mas não utilizado em CreatePet
```

### Padrões de Mocking
- **Stubs**: Para entradas (findById, existsBy)
- **Mocks**: Para saídas (save, delete)
- **Verificação**: De interações e ordem

---

## Fixtures e Helpers

### Métodos Auxiliares - CreatePetUseCaseTest
```kotlin
/**
 * FIXTURE: Cria uma requisição válida para criação de pet.
 * Simula o estado n3 (dados preenchidos pelo usuário).
 */
private fun createValidRequest(
    name: String = "Rex",
    species: String = "DOG",
    breed: String = "Labrador",
    gender: String = "MALE",
    age: Int = 3,
    weight: Double = 25.5,
    healthStatus: String = "GOOD",
    ownerName: String = "João Silva",
    ownerPhone: String = "(11) 99999-9999",
    healthHistory: String = "Sem histórico"
) = CreatePetRequest(...)

/**
 * FIXTURE: Cria uma entidade Pet para simular retorno do repositório.
 * Representa o estado n5 (pet salvo com sucesso no banco).
 */
private fun createPet(
    id: UUID = testPetId,
    name: String = "Rex",
    species: PetSpecies = PetSpecies.DOG,
    ...
) = Pet(...)
```