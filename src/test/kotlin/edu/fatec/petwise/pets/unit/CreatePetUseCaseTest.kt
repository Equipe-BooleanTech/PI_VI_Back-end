package edu.fatec.petwise.pets.unit

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.usecase.CreatePetUseCase
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.infrastructure.service.IotService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import java.time.LocalDateTime
import java.util.*

/**
 * =====================================================================
 * TESTES UNITÁRIOS - CreatePetUseCase (Cadastro de Pet)
 * =====================================================================
 * 
 * Caso de Uso: UC-PET-01 - Cadastrar Pet
 * 
 * =====================================================================
 * DESCRIÇÃO DO CASO DE USO
 * =====================================================================
 * 
 * Precondições:
 *   O Usuário deve ter realizado o Cadastro e Login no sistema.
 * 
 * Fluxo Principal:
 *   1) O Usuário acessa a opção "Adicionar Novo Pet" (Estímulo)
 *   2) O Sistema exibe o formulário de cadastro de pet (Resposta)
 *   3) O Usuário preenche e envia o formulário com nome, espécie, raça,
 *      idade (anos), peso e histórico de saúde (textual) (Estímulo)
 *   4) O Sistema valida os dados, associa o novo Pet ao ID do Usuário
 *      e registra o pet (Resposta)
 *   5) O Sistema exibe uma mensagem de sucesso e redireciona para a
 *      lista de pets cadastrados (Resposta)
 * 
 * Fluxo Alternativo (passo 3):
 *   a) Campo Opcional: O Usuário opta por não preencher o histórico de
 *      saúde (textual), enviando apenas os dados obrigatórios.
 *   b) Múltiplos Pets: Após o registro do primeiro pet, o usuário inicia
 *      novamente o processo para adicionar outro pet à sua conta.
 * 
 * Fluxo de Exceção (passo 4):
 *   a) Dados Obrigatórios Ausentes: O Usuário falha em fornecer o Nome,
 *      Espécie ou Data de Nascimento do Pet. O Sistema exibe uma mensagem
 *      de erro indicando os campos faltantes e impede o registro.
 * 
 * Pós-condições:
 *   O novo Pet está registrado no sistema e associado ao Usuário.
 *   O usuário pode visualizar e editar os dados do pet.
 * 
 * =====================================================================
 * REGRAS DE NEGÓCIO RELACIONADAS
 * =====================================================================
 * 
 * RN01 — Associação de Dono:
 *   Um usuário (tutor) deve poder cadastrar e gerenciar vários pets.
 *   → Testado em: SEQ1-01
 * 
 * RN02 — Dados Essenciais:
 *   Cada pet deve ter obrigatoriamente um registro de espécie, nome e
 *   data de nascimento para ser cadastrado.
 *   → Testado em: SEQ2-01 a SEQ2-05
 * 
 * RN03 — Proteção de Histórico:
 *   Não é permitido excluir um pet que já possua registros históricos
 *   (consultas, vacinas ou exames).
 *   → Validado em outros testes (DeletePetUseCase)
 * 
 * =====================================================================
 * GRAFO DE ESTADOS (GE) - SEQUÊNCIAS INDEPENDENTES
 * =====================================================================
 * 
 * Estados do Grafo:
 *   n1 = Início (usuário logado)
 *   n2 = Formulário de cadastro exibido
 *   n3 = Dados preenchidos pelo usuário
 *   n4 = Validação dos dados
 *   n5 = Pet salvo com sucesso
 *   n6 = Fim (redirecionamento para lista)
 *   n7 = Erro de validação (retorno ao formulário)
 * 
 * Sequência 1 (Caminho Feliz):
 *   n1 → n2 → n3 → n4 → n5 → n6
 *   Fluxo principal completo do cadastro de pet, sem ocorrência de erros.
 *   Representa o cenário ideal onde o usuário preenche os dados
 *   corretamente na primeira tentativa.
 * 
 * Sequência 2 (Erro de Validação):
 *   n1 → n2 → n3 → n4 → n7 → n2
 *   Caminho independente que exercita exclusivamente o desvio por erro
 *   de validação. O sistema detecta dados inválidos e retorna o usuário
 *   ao formulário inicial.
 * 
 * Sequência 3 (Erro + Correção):
 *   n1 → n2 → n3 → n4 → n7 → n2 → n3 → n4 → n5 → n6
 *   Caminho completo com erro seguido de correção. Após a validação
 *   falhar, o usuário corrige os dados e o cadastro finaliza com sucesso.
 *   Testa o loop de retorno ao estado n2 e posterior continuação do
 *   fluxo principal.
 * 
 * =====================================================================
 * PADRÕES DE TESTE UTILIZADOS
 * =====================================================================
 * 
 * - MOCK: PetRepository e IotService são mockados para isolar a unidade
 *   Biblioteca: MockK (io.mockk)
 *   O IotService é mockado mas NÃO utilizado (foco apenas em criação)
 * 
 * - STUB: Respostas pré-definidas para métodos dos mocks
 *   Exemplo: every { petRepository.save(any()) } returns savedPet
 * 
 * - FIXTURE: Métodos auxiliares createValidRequest() e createPet()
 *   Garante consistência e reutilização dos dados de teste
 * 
 * =====================================================================
 * CASOS DE TESTE
 * =====================================================================
 * 
 * SEQUÊNCIA 1 - Fluxo Principal (n1 → n2 → n3 → n4 → n5 → n6):
 *   SEQ1-01: Cadastro completo com todos os campos obrigatórios
 *   SEQ1-02: Cadastro com campo opcional (histórico) vazio
 *   SEQ1-03: Cadastro de múltiplos pets pelo mesmo usuário
 *   SEQ1-04: Normalização de campos (trim de espaços)
 *   SEQ1-05: Conversão de enums (lowercase → uppercase)
 * 
 * SEQUÊNCIA 2 - Erro de Validação (n1 → n2 → n3 → n4 → n7 → n2):
 *   SEQ2-01: Rejeição por nome vazio
 *   SEQ2-02: Rejeição por nome apenas com espaços
 *   SEQ2-03: Rejeição por espécie inválida
 *   SEQ2-04: Rejeição por espécie vazia
 *   SEQ2-05: Rejeição por raça vazia
 *   SEQ2-06: Rejeição por idade negativa
 *   SEQ2-07: Rejeição por peso zero ou negativo
 * 
 * SEQUÊNCIA 3 - Erro + Correção (simulado via múltiplas chamadas):
 *   SEQ3-01: Primeira tentativa falha, segunda sucesso
 * 
 * @author Equipe PetWise - BooleanTech
 * @version 1.0
 * @see CreatePetUseCase
 * @see Pet
 */
@DisplayName("CreatePetUseCase - UC-PET-01: Cadastrar Pet")
class CreatePetUseCaseTest {

    // =====================================================================
    // MOCKS - Dependências mockadas para isolamento da unidade de teste
    // =====================================================================
    
    /**
     * MOCK: PetRepository
     * Simula o repositório de pets para evitar acesso ao banco de dados real.
     * Utilizado para verificar se o pet foi salvo corretamente (estado n5).
     */
    @MockK
    private lateinit var petRepository: PetRepository
    
    /**
     * MOCK: IotService
     * Simula o serviço de IoT (não utilizado nestes testes, apenas para
     * satisfazer a dependência do construtor do UseCase).
     */
    @MockK
    private lateinit var iotService: IotService
    
    // Use Case sob teste (SUT - System Under Test)
    private lateinit var createPetUseCase: CreatePetUseCase
    
    // Fixtures - Dados de teste reutilizáveis
    private val testUserId = UUID.randomUUID()
    private val testPetId = UUID.randomUUID()
    private val now = LocalDateTime.now()
    
    @BeforeEach
    fun setUp() {
        // Inicializa os mocks anotados com @MockK
        MockKAnnotations.init(this)
        // Instancia o UseCase com as dependências mockadas
        createPetUseCase = CreatePetUseCase(petRepository, iotService)
    }
    
    @AfterEach
    fun tearDown() {
        // Limpa todos os mocks após cada teste
        clearAllMocks()
    }
    
    // =====================================================================
    // FIXTURES - Métodos auxiliares para criar dados de teste
    // =====================================================================
    
    /**
     * FIXTURE: Cria uma requisição válida para criação de pet.
     * Simula o estado n3 (dados preenchidos pelo usuário).
     * Contém todos os campos obrigatórios preenchidos corretamente.
     * Parâmetros podem ser sobrescritos para testar cenários específicos.
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
    ) = CreatePetRequest(
        name = name,
        breed = breed,
        species = species,
        gender = gender,
        age = age,
        weight = weight,
        healthStatus = healthStatus,
        ownerName = ownerName,
        ownerPhone = ownerPhone,
        healthHistory = healthHistory,
        profileImageUrl = null,
        shouldPairNfc = false,  // Sempre false - sem IoT para fins de teste
        readerId = null         // Sempre null - sem IoT para fins de teste
    )
    
    /**
     * FIXTURE: Cria uma entidade Pet para simular retorno do repositório.
     * Representa o estado n5 (pet salvo com sucesso no banco).
     */
    private fun createPet(
        id: UUID = testPetId,
        name: String = "Rex",
        species: PetSpecies = PetSpecies.DOG,
        breed: String = "Labrador",
        gender: PetGender = PetGender.MALE,
        age: Int = 3,
        weight: Double = 25.5,
        healthStatus: HealthStatus = HealthStatus.GOOD,
        ownerId: UUID = testUserId
    ) = Pet(
        id = id,
        ownerId = ownerId,
        name = name,
        breed = breed,
        species = species,
        gender = gender,
        age = age,
        weight = weight,
        healthStatus = healthStatus,
        ownerName = "João Silva",
        ownerPhone = "(11) 99999-9999",
        healthHistory = "Sem histórico",
        createdAt = now,
        updatedAt = now
    )

    // =====================================================================
    // SEQUÊNCIA 1 - FLUXO PRINCIPAL (n1 → n2 → n3 → n4 → n5 → n6)
    // Caminho feliz: cadastro completo sem erros
    // =====================================================================
    
    @Nested
    @DisplayName("Sequência 1: Fluxo Principal (n1 → n2 → n3 → n4 → n5 → n6)")
    inner class Sequencia1FluxoPrincipal {
        
        /**
         * SEQ1-01: Teste do fluxo principal completo
         * 
         * Caminho: n1 → n2 → n3 → n4 → n5 → n6
         * 
         * Descrição: Fluxo principal completo do cadastro de pet, sem
         * ocorrência de erros. Representa o cenário ideal onde o usuário
         * preenche os dados corretamente na primeira tentativa.
         * 
         * Regras validadas:
         * - RN01: Associação de Dono (pet vinculado ao userId)
         * - RN02: Dados Essenciais (nome, espécie preenchidos)
         */
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
            assertEquals("Rex", result.name, "Nome deve ser Rex")
            assertEquals("DOG", result.species, "Espécie deve ser DOG")
            assertEquals(testUserId, result.ownerId, "Pet deve estar vinculado ao usuário (RN01)")
            assertEquals("Labrador", result.breed, "Raça deve ser Labrador")
            assertEquals("MALE", result.gender, "Gênero deve ser MALE")
            assertEquals(3, result.age, "Idade deve ser 3")
            assertEquals(25.5, result.weight, "Peso deve ser 25.5")
            
            // Verificar que o repositório foi chamado (transição n4 → n5)
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        /**
         * SEQ1-02: Teste do fluxo alternativo - campo opcional vazio
         * 
         * Caminho: n1 → n2 → n3 → n4 → n5 → n6
         * 
         * Descrição: Fluxo alternativo (passo 3a) onde o usuário opta por
         * não preencher o histórico de saúde (campo opcional).
         */
        @Test
        @DisplayName("SEQ1-02: Deve cadastrar pet sem histórico de saúde (campo opcional)")
        fun `deve cadastrar pet sem historico de saude`() {
            // Arrange - Estado n3: dados com campo opcional vazio
            val request = createValidRequest(healthHistory = "")
            val savedPet = createPet()
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            Assertions.assertNotNull(result)
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        /**
         * SEQ1-03: Teste do fluxo alternativo - múltiplos pets
         * 
         * Caminho: (n1 → n2 → n3 → n4 → n5 → n6) × 2
         * 
         * Descrição: Fluxo alternativo (passo 3b) onde o usuário cadastra
         * múltiplos pets sequencialmente. Valida RN01.
         */
        @Test
        @DisplayName("SEQ1-03: Deve permitir cadastro de múltiplos pets (RN01)")
        fun `deve permitir cadastro de multiplos pets`() {
            // Arrange - Dois pets diferentes
            val request1 = createValidRequest(name = "Rex", species = "DOG")
            val request2 = createValidRequest(name = "Mimi", species = "CAT")
            
            val pet1 = createPet(id = UUID.randomUUID(), name = "Rex", species = PetSpecies.DOG)
            val pet2 = createPet(id = UUID.randomUUID(), name = "Mimi", species = PetSpecies.CAT)
            
            every { petRepository.save(any()) } returnsMany listOf(pet1, pet2)
            
            // Act - Primeiro cadastro
            val result1 = createPetUseCase.execute(testUserId, request1)
            // Act - Segundo cadastro (mesmo usuário)
            val result2 = createPetUseCase.execute(testUserId, request2)
            
            // Assert - Ambos vinculados ao mesmo usuário
            assertEquals("Rex", result1.name)
            assertEquals("Mimi", result2.name)
            assertEquals(testUserId, result1.ownerId, "Primeiro pet vinculado ao usuário")
            assertEquals(testUserId, result2.ownerId, "Segundo pet vinculado ao usuário")
            
            verify(exactly = 2) { petRepository.save(any()) }
        }
        
        /**
         * SEQ1-04: Teste de normalização de campos (trim)
         * 
         * Caminho: n1 → n2 → n3 → n4 → n5 → n6
         * 
         * Descrição: O sistema deve normalizar os dados antes de salvar,
         * removendo espaços extras do início e fim dos campos.
         */
        @Test
        @DisplayName("SEQ1-04: Deve aplicar trim nos campos de texto")
        fun `deve aplicar trim nos campos de texto`() {
            // Arrange - Request com espaços extras
            val request = createValidRequest(
                name = "  Rex  ",
                breed = "  Labrador  ",
                ownerName = "  João Silva  "
            )
            val savedPet = createPet()
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            createPetUseCase.execute(testUserId, request)
            
            // Assert - Verificar normalização
            verify {
                petRepository.save(match { pet ->
                    pet.name == "Rex" &&
                    pet.breed == "Labrador" &&
                    pet.ownerName == "João Silva"
                })
            }
        }
        
        /**
         * SEQ1-05: Teste de conversão de enums (case insensitive)
         * 
         * Caminho: n1 → n2 → n3 → n4 → n5 → n6
         * 
         * Descrição: O sistema deve aceitar valores de enum em qualquer
         * case e converter para uppercase antes de processar.
         */
        @Test
        @DisplayName("SEQ1-05: Deve aceitar espécie/gênero em lowercase")
        fun `deve aceitar especie e genero em lowercase`() {
            // Arrange - Enums em lowercase
            val request = createValidRequest(species = "dog", gender = "female")
            val savedPet = createPet(species = PetSpecies.DOG, gender = PetGender.FEMALE)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("DOG", result.species, "Espécie convertida para uppercase")
            assertEquals("FEMALE", result.gender, "Gênero convertido para uppercase")
        }
    }

    // =====================================================================
    // SEQUÊNCIA 2 - ERRO DE VALIDAÇÃO (n1 → n2 → n3 → n4 → n7 → n2)
    // Desvio por dados inválidos - retorno ao formulário
    // =====================================================================
    
    @Nested
    @DisplayName("Sequência 2: Erro de Validação (n1 → n2 → n3 → n4 → n7 → n2)")
    inner class Sequencia2ErroValidacao {
        
        /**
         * SEQ2-01: Teste de rejeição por nome vazio
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         * 
         * Descrição: Fluxo de exceção (passo 4a) - O Usuário falha em
         * fornecer o Nome do Pet. O Sistema exibe mensagem de erro.
         * 
         * Regra validada: RN02 (Dados Essenciais)
         */
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
            
            // Verificar que NÃO atingiu estado n5 (save não chamado)
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * SEQ2-02: Teste de rejeição por nome com apenas espaços
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         * 
         * Regra validada: RN02 (Dados Essenciais)
         */
        @Test
        @DisplayName("SEQ2-02: Deve rejeitar nome apenas com espaços (RN02)")
        fun `deve rejeitar nome com espacos em branco`() {
            // Arrange
            val request = createValidRequest(name = "   ")
            
            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            assertEquals("Nome do pet é obrigatório", exception.message)
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * SEQ2-03: Teste de rejeição por espécie inválida
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         * 
         * Descrição: Fluxo de exceção (passo 4a) - O Usuário fornece
         * uma Espécie que não existe no sistema.
         * 
         * Regra validada: RN02 (Dados Essenciais)
         */
        @Test
        @DisplayName("SEQ2-03: Deve rejeitar espécie inválida (RN02)")
        fun `deve rejeitar especie invalida`() {
            // Arrange - Espécie não existente no enum
            val request = createValidRequest(species = "DRAGON")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * SEQ2-04: Teste de rejeição por espécie vazia
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         * 
         * Regra validada: RN02 (Dados Essenciais)
         */
        @Test
        @DisplayName("SEQ2-04: Deve rejeitar espécie vazia (RN02)")
        fun `deve rejeitar especie vazia`() {
            // Arrange
            val request = createValidRequest(species = "")
            
            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            assertEquals("Espécie é obrigatória", exception.message)
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * SEQ2-05: Teste de rejeição por raça vazia
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         * 
         * Regra validada: RN02 (Dados Essenciais)
         */
        @Test
        @DisplayName("SEQ2-05: Deve rejeitar raça vazia (RN02)")
        fun `deve rejeitar raca vazia`() {
            // Arrange
            val request = createValidRequest(breed = "")
            
            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            assertEquals("Raça é obrigatória", exception.message)
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * SEQ2-06: Teste de rejeição por idade negativa
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         */
        @Test
        @DisplayName("SEQ2-06: Deve rejeitar idade negativa")
        fun `deve rejeitar idade negativa`() {
            // Arrange
            val request = createValidRequest(age = -1)
            
            // Act & Assert
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            assertEquals("Idade deve ser maior ou igual a 0", exception.message)
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * SEQ2-07: Teste de rejeição por peso zero ou negativo
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7
         */
        @Test
        @DisplayName("SEQ2-07: Deve rejeitar peso zero ou negativo")
        fun `deve rejeitar peso zero ou negativo`() {
            // Arrange - Peso zero
            val request1 = createValidRequest(weight = 0.0)
            
            // Act & Assert
            val exception1 = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request1)
            }
            assertEquals("Peso deve ser um valor positivo", exception1.message)
            
            // Arrange - Peso negativo
            val request2 = createValidRequest(weight = -5.0)
            
            val exception2 = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request2)
            }
            assertEquals("Peso deve ser um valor positivo", exception2.message)
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    // =====================================================================
    // SEQUÊNCIA 3 - ERRO + CORREÇÃO
    // (n1 → n2 → n3 → n4 → n7 → n2 → n3 → n4 → n5 → n6)
    // Primeira tentativa falha, segunda sucesso
    // =====================================================================
    
    @Nested
    @DisplayName("Sequência 3: Erro + Correção (n1 → ... → n7 → n2 → ... → n6)")
    inner class Sequencia3ErroCorrecao {
        
        /**
         * SEQ3-01: Teste de erro seguido de correção
         * 
         * Caminho: n1 → n2 → n3 → n4 → n7 → n2 → n3 → n4 → n5 → n6
         * 
         * Descrição: Caminho completo com erro seguido de correção.
         * Após a validação falhar, o usuário corrige os dados e o
         * cadastro finaliza com sucesso. Testa o loop de retorno ao
         * estado n2 e posterior continuação do fluxo principal.
         */
        @Test
        @DisplayName("SEQ3-01: Deve permitir correção após erro de validação")
        fun `deve permitir correcao apos erro de validacao`() {
            // ===== PRIMEIRA TENTATIVA (falha) =====
            // Estado n3: dados inválidos (nome vazio)
            val requestInvalido = createValidRequest(name = "")
            
            // Transição n4 → n7: erro de validação
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, requestInvalido)
            }
            assertEquals("Nome do pet é obrigatório", exception.message)
            
            // Verificar que não salvou (não atingiu n5)
            verify(exactly = 0) { petRepository.save(any()) }
            
            // ===== SEGUNDA TENTATIVA (sucesso) =====
            // Estado n2 → n3: usuário corrige os dados
            val requestValido = createValidRequest(name = "Rex")
            val savedPet = createPet(name = "Rex")
            
            // STUB: Simula estado n5 (pet salvo)
            every { petRepository.save(any()) } returns savedPet
            
            // Transição n3 → n4 → n5 → n6
            val result = createPetUseCase.execute(testUserId, requestValido)
            
            // Assert - Estado n6 atingido com sucesso
            Assertions.assertNotNull(result)
            assertEquals("Rex", result.name)
            assertEquals(testUserId, result.ownerId)
            
            // Verificar que salvou na segunda tentativa
            verify(exactly = 1) { petRepository.save(any()) }
        }
    }
}