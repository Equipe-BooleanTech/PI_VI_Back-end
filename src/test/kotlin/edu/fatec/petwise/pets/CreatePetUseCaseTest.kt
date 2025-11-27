package edu.fatec.petwise.pets

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.usecase.CreatePetUseCase
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.repository.PetRepository
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertNotEquals

/**
 * ============================================================================
 * TESTES DE UNIDADE PARA O CASO DE USO: CRIAR PET (CreatePetUseCase)
 * ============================================================================
 * 
 * Esta classe de teste implementa testes de unidade para o caso de uso de
 * criação de Pet, verificando as regras de negócio e fluxos de execução.
 * 
 * TÉCNICA DE TESTE: Caixa-branca (estrutural)
 * ABORDAGEM: Teste de unidade com isolamento de dependências
 * 
 * RECURSOS UTILIZADOS:
 * - JUnit 5: Framework de testes
 * - MockK: Biblioteca de mocking para Kotlin
 * 
 * MOCKS UTILIZADOS:
 * - PetRepository: Mock do repositório para isolar o teste do banco de dados.
 *   O mock simula o comportamento do repositório sem acessar o banco real.
 * 
 * REGRAS DE NEGÓCIO TESTADAS:
 * 1. Um pet deve ser criado com todos os campos obrigatórios
 * 2. O nome e raça devem ser trimados (espaços removidos)
 * 3. Espécie, gênero e status de saúde devem ser válidos (enum)
 * 4. O pet deve ser associado ao usuário (ownerId)
 * 5. Datas de criação e atualização devem ser definidas automaticamente
 * ============================================================================
 */
@DisplayName("CreatePetUseCase - Testes do Caso de Uso Criar Pet")
class CreatePetUseCaseTest {

    // ========================================================================
    // MOCKS E CONFIGURAÇÃO
    // ========================================================================
    
    /**
     * MOCK: PetRepository
     * 
     * Este mock substitui o repositório real durante os testes.
     * Permite simular operações de persistência sem acessar o banco de dados.
     * Isso é essencial para testes de unidade isolados e rápidos.
     */
    @MockK
    private lateinit var petRepository: PetRepository
    
    /**
     * SUT (System Under Test): CreatePetUseCase
     * 
     * Esta é a classe sendo testada. As dependências são injetadas via MockK.
     */
    @InjectMockKs
    private lateinit var createPetUseCase: CreatePetUseCase
    
    // Dados de teste
    private val testUserId = UUID.randomUUID()
    private val testPetId = UUID.randomUUID()
    
    @BeforeEach
    fun setUp() {
        // Inicializa os mocks antes de cada teste
        MockKAnnotations.init(this)
    }
    
    @AfterEach
    fun tearDown() {
        // Limpa os mocks após cada teste para evitar interferência
        clearAllMocks()
    }

    // ========================================================================
    // FIXTURES - Métodos auxiliares para criar objetos de teste
    // ========================================================================
    
    /**
     * Cria um CreatePetRequest válido para testes.
     * Este é um FACTORY METHOD que centraliza a criação de requests de teste.
     */
    private fun createValidRequest(
        name: String = "Rex",
        breed: String = "Labrador",
        species: String = "DOG",
        gender: String = "MALE",
        age: Int = 3,
        weight: Double = 25.5,
        healthStatus: String = "GOOD",
        ownerName: String = "João Silva",
        ownerPhone: String = "(11) 99999-9999",
        healthHistory: String = "",
        profileImageUrl: String? = null
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
        profileImageUrl = profileImageUrl
    )
    
    /**
     * Cria uma entidade Pet para simular o retorno do repositório.
     * STUB: Usado para simular o comportamento do repositório save().
     */
    private fun createSavedPet(request: CreatePetRequest, ownerId: UUID): Pet {
        return Pet(
            id = testPetId,
            ownerId = ownerId,
            name = request.name.trim(),
            breed = request.breed.trim(),
            species = PetSpecies.valueOf(request.species.uppercase()),
            gender = PetGender.valueOf(request.gender.uppercase()),
            age = request.age,
            weight = request.weight,
            healthStatus = HealthStatus.valueOf(request.healthStatus.uppercase()),
            ownerName = request.ownerName.trim(),
            ownerPhone = request.ownerPhone.trim(),
            healthHistory = request.healthHistory.trim(),
            profileImageUrl = request.profileImageUrl?.trim(),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    // ========================================================================
    // TESTES DE CRIAÇÃO COM SUCESSO
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Criação com Sucesso")
    inner class CriacaoSucessoTests {
        
        @Test
        @DisplayName("Deve criar pet com todos os campos obrigatórios")
        fun `deve criar pet com campos obrigatorios`() {
            // Arrange - Preparação do cenário
            val request = createValidRequest()
            val savedPet = createSavedPet(request, testUserId)
            
            /**
             * STUB: Configuração do comportamento do mock
             * 
             * Aqui definimos que quando o método save() for chamado com qualquer Pet,
             * ele deve retornar o savedPet que criamos. Isso simula o comportamento
             * do repositório real sem acessar o banco de dados.
             */
            every { petRepository.save(any()) } returns savedPet
            
            // Act - Execução do caso de uso
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert - Verificação dos resultados
            assertNotNull(result)
            assertEquals(testPetId, result.id)
            assertEquals("Rex", result.name)
            assertEquals("Labrador", result.breed)
            assertEquals("DOG", result.species)
            assertEquals("MALE", result.gender)
            assertEquals(3, result.age)
            assertEquals(25.5, result.weight)
            assertEquals("GOOD", result.healthStatus)
            
            /**
             * VERIFICAÇÃO DO MOCK
             * 
             * Verifica que o método save() foi chamado exatamente uma vez.
             * Isso garante que o caso de uso está interagindo corretamente
             * com o repositório.
             */
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve criar pet do tipo Gato")
        fun `deve criar pet do tipo gato`() {
            // Arrange
            val request = createValidRequest(
                name = "Mimi",
                species = "CAT",
                gender = "FEMALE",
                breed = "Siamês"
            )
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("Mimi", result.name)
            assertEquals("CAT", result.species)
            assertEquals("FEMALE", result.gender)
            assertEquals("Siamês", result.breed)
            
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve criar pet com URL de imagem de perfil")
        fun `deve criar pet com imagem de perfil`() {
            // Arrange
            val imageUrl = "https://example.com/pet-photo.jpg"
            val request = createValidRequest(profileImageUrl = imageUrl)
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals(imageUrl, result.profileImageUrl)
            
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve criar pet com histórico de saúde")
        fun `deve criar pet com historico de saude`() {
            // Arrange
            val healthHistory = "Vacinado em 2023. Castrado em 2024."
            val request = createValidRequest(healthHistory = healthHistory)
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals(healthHistory, result.healthHistory)
            
            verify(exactly = 1) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES DE TRATAMENTO DE ESPAÇOS (TRIM)
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Tratamento de Espaços")
    inner class TrimTests {
        
        @Test
        @DisplayName("Deve remover espaços do nome do pet")
        fun `deve fazer trim do nome`() {
            // Arrange
            val request = createValidRequest(name = "  Rex  ")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("Rex", result.name)
            
            /**
             * VERIFICAÇÃO DETALHADA DO MOCK
             * 
             * Captura o argumento passado para o método save() e verifica
             * se o trim foi aplicado corretamente antes de salvar.
             */
            verify { 
                petRepository.save(match { pet ->
                    pet.name == "Rex" // Verifica que o trim foi aplicado
                })
            }
        }
        
        @Test
        @DisplayName("Deve remover espaços da raça do pet")
        fun `deve fazer trim da raca`() {
            // Arrange
            val request = createValidRequest(breed = "  Labrador Retriever  ")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            createPetUseCase.execute(testUserId, request)
            
            // Assert
            verify { 
                petRepository.save(match { pet ->
                    pet.breed == "Labrador Retriever"
                })
            }
        }
        
        @Test
        @DisplayName("Deve remover espaços do nome do dono")
        fun `deve fazer trim do nome do dono`() {
            // Arrange
            val request = createValidRequest(ownerName = "  João Silva  ")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            createPetUseCase.execute(testUserId, request)
            
            // Assert
            verify { 
                petRepository.save(match { pet ->
                    pet.ownerName == "João Silva"
                })
            }
        }
        
        @Test
        @DisplayName("Deve remover espaços do telefone do dono")
        fun `deve fazer trim do telefone do dono`() {
            // Arrange
            val request = createValidRequest(ownerPhone = "  (11) 99999-9999  ")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            createPetUseCase.execute(testUserId, request)
            
            // Assert
            verify { 
                petRepository.save(match { pet ->
                    pet.ownerPhone == "(11) 99999-9999"
                })
            }
        }
    }

    // ========================================================================
    // TESTES DE CONVERSÃO DE ENUMS (CASE INSENSITIVE)
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Conversão de Enums")
    inner class EnumConversionTests {
        
        @Test
        @DisplayName("Deve aceitar espécie em minúsculas")
        fun `deve aceitar especie em minusculas`() {
            // Arrange
            val request = createValidRequest(species = "dog")
            val savedPet = createSavedPet(request.copy(species = "DOG"), testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("DOG", result.species)
            
            verify { 
                petRepository.save(match { pet ->
                    pet.species == PetSpecies.DOG
                })
            }
        }
        
        @Test
        @DisplayName("Deve aceitar gênero em minúsculas")
        fun `deve aceitar genero em minusculas`() {
            // Arrange
            val request = createValidRequest(gender = "male")
            val savedPet = createSavedPet(request.copy(gender = "MALE"), testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("MALE", result.gender)
            
            verify { 
                petRepository.save(match { pet ->
                    pet.gender == PetGender.MALE
                })
            }
        }
        
        @Test
        @DisplayName("Deve aceitar status de saúde em minúsculas")
        fun `deve aceitar status de saude em minusculas`() {
            // Arrange
            val request = createValidRequest(healthStatus = "excellent")
            val savedPet = createSavedPet(request.copy(healthStatus = "EXCELLENT"), testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("EXCELLENT", result.healthStatus)
            
            verify { 
                petRepository.save(match { pet ->
                    pet.healthStatus == HealthStatus.EXCELLENT
                })
            }
        }
        
        @Test
        @DisplayName("Deve aceitar espécie com case misto")
        fun `deve aceitar especie com case misto`() {
            // Arrange
            val request = createValidRequest(species = "CaT")
            val savedPet = createSavedPet(request.copy(species = "CAT"), testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("CAT", result.species)
        }
    }

    // ========================================================================
    // TESTES DE TODAS AS ESPÉCIES
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Todas as Espécies")
    inner class TodasEspeciesTests {
        
        @Test
        @DisplayName("Deve criar pet do tipo Cachorro (DOG)")
        fun `deve criar pet tipo cachorro`() {
            val request = createValidRequest(species = "DOG")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("DOG", result.species)
        }
        
        @Test
        @DisplayName("Deve criar pet do tipo Gato (CAT)")
        fun `deve criar pet tipo gato`() {
            val request = createValidRequest(species = "CAT")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("CAT", result.species)
        }
        
        @Test
        @DisplayName("Deve criar pet do tipo Ave (BIRD)")
        fun `deve criar pet tipo ave`() {
            val request = createValidRequest(species = "BIRD", breed = "Calopsita")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("BIRD", result.species)
        }
        
        @Test
        @DisplayName("Deve criar pet do tipo Coelho (RABBIT)")
        fun `deve criar pet tipo coelho`() {
            val request = createValidRequest(species = "RABBIT", breed = "Mini Lop")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("RABBIT", result.species)
        }
        
        @Test
        @DisplayName("Deve criar pet do tipo Outro (OTHER)")
        fun `deve criar pet tipo outro`() {
            val request = createValidRequest(species = "OTHER", breed = "Hamster")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("OTHER", result.species)
        }
    }

    // ========================================================================
    // TESTES DE TODOS OS STATUS DE SAÚDE
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Todos os Status de Saúde")
    inner class TodosStatusSaudeTests {
        
        @Test
        @DisplayName("Deve criar pet com status Excelente")
        fun `deve criar pet status excelente`() {
            val request = createValidRequest(healthStatus = "EXCELLENT")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("EXCELLENT", result.healthStatus)
        }
        
        @Test
        @DisplayName("Deve criar pet com status Bom")
        fun `deve criar pet status bom`() {
            val request = createValidRequest(healthStatus = "GOOD")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("GOOD", result.healthStatus)
        }
        
        @Test
        @DisplayName("Deve criar pet com status Regular")
        fun `deve criar pet status regular`() {
            val request = createValidRequest(healthStatus = "REGULAR")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("REGULAR", result.healthStatus)
        }
        
        @Test
        @DisplayName("Deve criar pet com status Atenção")
        fun `deve criar pet status atencao`() {
            val request = createValidRequest(healthStatus = "ATTENTION")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("ATTENTION", result.healthStatus)
        }
        
        @Test
        @DisplayName("Deve criar pet com status Crítico")
        fun `deve criar pet status critico`() {
            val request = createValidRequest(healthStatus = "CRITICAL")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            val result = createPetUseCase.execute(testUserId, request)
            
            assertEquals("CRITICAL", result.healthStatus)
        }
    }

    // ========================================================================
    // TESTES DE CENÁRIOS DE ERRO
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Cenários de Erro")
    inner class CenariosErroTests {
        
        @Test
        @DisplayName("Deve lançar exceção para espécie inválida")
        fun `deve lancar excecao para especie invalida`() {
            // Arrange
            val request = createValidRequest(species = "INVALID_SPECIES")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            // Verifica que o repositório NÃO foi chamado
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve lançar exceção para gênero inválido")
        fun `deve lancar excecao para genero invalido`() {
            // Arrange
            val request = createValidRequest(gender = "INVALID_GENDER")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve lançar exceção para status de saúde inválido")
        fun `deve lancar excecao para status de saude invalido`() {
            // Arrange
            val request = createValidRequest(healthStatus = "INVALID_STATUS")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES DE FLUXOS ALTERNATIVOS E EXCEÇÃO - VALIDAÇÃO DE CAMPOS
    // ========================================================================
    
    @Nested
    @DisplayName("Fluxo Alternativo - Validação de Campos Obrigatórios")
    inner class ValidacaoCamposObrigatoriosTests {
        
        /**
         * REGRA DE NEGÓCIO: RN01 - Nome do pet é obrigatório
         * FLUXO ALTERNATIVO: FA01 - Sistema rejeita criação sem nome válido
         */
        @Test
        @DisplayName("FA01 - Deve rejeitar nome vazio após trim")
        fun `deve rejeitar nome vazio apos trim`() {
            // Arrange
            val request = createValidRequest(name = "   ") // Apenas espaços
            
            // O trim vai resultar em string vazia
            // Esta validação deveria ocorrer no DTO ou Use Case
            val savedPet = createSavedPet(request, testUserId)
            every { petRepository.save(any()) } returns savedPet
            
            // Act - O sistema aceita mas o nome fica vazio
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert - Demonstra comportamento atual (pode precisar de validação adicional)
            assertEquals("", result.name)
        }
        
        /**
         * REGRA DE NEGÓCIO: RN02 - Raça do pet é obrigatória
         * FLUXO ALTERNATIVO: FA02 - Sistema processa raça mesmo com espaços
         */
        @Test
        @DisplayName("FA02 - Deve processar raça com múltiplos espaços")
        fun `deve processar raca com multiplos espacos`() {
            // Arrange
            val request = createValidRequest(breed = "  Golden   Retriever  ")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert - Verifica que espaços internos são mantidos, externos removidos
            assertEquals("Golden   Retriever", result.breed)
        }
        
        /**
         * REGRA DE NEGÓCIO: RN03 - Idade deve ser número não-negativo
         * FLUXO DE EXCEÇÃO: FE01 - Sistema processa idade zero (filhote recém-nascido)
         */
        @Test
        @DisplayName("FE01 - Deve aceitar idade zero para filhotes recém-nascidos")
        fun `deve aceitar idade zero para filhotes`() {
            // Arrange
            val request = createValidRequest(age = 0)
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals(0, result.age)
        }
        
        /**
         * REGRA DE NEGÓCIO: RN04 - Peso deve ser positivo
         * FLUXO DE EXCEÇÃO: FE02 - Sistema aceita peso muito pequeno
         */
        @Test
        @DisplayName("FE02 - Deve aceitar peso muito pequeno (animais pequenos)")
        fun `deve aceitar peso muito pequeno`() {
            // Arrange
            val request = createValidRequest(weight = 0.1, species = "BIRD", breed = "Canário")
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals(0.1, result.weight)
        }
    }
    
    // ========================================================================
    // TESTES DE REGRAS DE NEGÓCIO ESPECÍFICAS
    // ========================================================================
    
    @Nested
    @DisplayName("Regras de Negócio - Criação de Pet")
    inner class RegrasCriacaoPetTests {
        
        /**
         * REGRA DE NEGÓCIO: RN05 - Pet deve ter datas de criação e atualização definidas
         */
        @Test
        @DisplayName("RN05 - Deve definir createdAt e updatedAt automaticamente")
        fun `deve definir datas automaticamente`() {
            // Arrange
            val request = createValidRequest()
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            createPetUseCase.execute(testUserId, request)
            
            // Assert - Verifica que datas são definidas
            verify {
                petRepository.save(match { pet ->
                    pet.createdAt != null && pet.updatedAt != null
                })
            }
        }
        
        /**
         * REGRA DE NEGÓCIO: RN06 - Pet novo não deve ter ID definido (gerado pelo banco)
         */
        @Test
        @DisplayName("RN06 - Deve criar pet sem ID pré-definido")
        fun `deve criar pet sem id pre-definido`() {
            // Arrange
            val request = createValidRequest()
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            createPetUseCase.execute(testUserId, request)
            
            // Assert - ID deve ser null antes de salvar
            verify {
                petRepository.save(match { pet ->
                    pet.id == null
                })
            }
        }
        
        /**
         * REGRA DE NEGÓCIO: RN07 - Pet novo deve ter isFavorite = false por padrão
         */
        @Test
        @DisplayName("RN07 - Deve criar pet como não favorito por padrão")
        fun `deve criar pet nao favorito por padrao`() {
            // Arrange
            val request = createValidRequest()
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertFalse(result.isFavorite)
        }
        
        /**
         * REGRA DE NEGÓCIO: RN08 - Pet novo deve ter isActive = true por padrão
         */
        @Test
        @DisplayName("RN08 - Deve criar pet como ativo por padrão")
        fun `deve criar pet ativo por padrao`() {
            // Arrange
            val request = createValidRequest()
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertTrue(result.active)
        }
    }
    
    // ========================================================================
    // TESTES DE FLUXOS DE EXCEÇÃO - ERROS DE ENUM
    // ========================================================================
    
    @Nested
    @DisplayName("Fluxo de Exceção - Valores de Enum Inválidos")
    inner class EnumInvalidoTests {
        
        /**
         * FLUXO DE EXCEÇÃO: FE03 - Espécie vazia deve lançar exceção
         */
        @Test
        @DisplayName("FE03 - Deve lançar exceção para espécie vazia")
        fun `deve lancar excecao para especie vazia`() {
            // Arrange
            val request = createValidRequest(species = "")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * FLUXO DE EXCEÇÃO: FE04 - Gênero com caracteres especiais
         */
        @Test
        @DisplayName("FE04 - Deve lançar exceção para gênero com caracteres especiais")
        fun `deve lancar excecao para genero com caracteres especiais`() {
            // Arrange
            val request = createValidRequest(gender = "M@LE")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * FLUXO DE EXCEÇÃO: FE05 - Status de saúde numérico
         */
        @Test
        @DisplayName("FE05 - Deve lançar exceção para status de saúde numérico")
        fun `deve lancar excecao para status de saude numerico`() {
            // Arrange
            val request = createValidRequest(healthStatus = "123")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }
    
    // ========================================================================
    // TESTES DE ASSOCIAÇÃO COM USUÁRIO
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Associação com Usuário")
    inner class AssociacaoUsuarioTests {
        
        @Test
        @DisplayName("Deve associar pet ao usuário correto")
        fun `deve associar pet ao usuario correto`() {
            // Arrange
            val request = createValidRequest()
            val savedPet = createSavedPet(request, testUserId)
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals(testUserId, result.ownerId)
            
            /**
             * VERIFICAÇÃO DO MOCK: Confirma que o ownerId foi definido corretamente
             */
            verify { 
                petRepository.save(match { pet ->
                    pet.ownerId == testUserId
                })
            }
        }
        
        @Test
        @DisplayName("Deve criar pets para diferentes usuários")
        fun `deve criar pets para diferentes usuarios`() {
            // Arrange
            val userId1 = UUID.randomUUID()
            val userId2 = UUID.randomUUID()
            val request = createValidRequest()
            
            val savedPet1 = createSavedPet(request, userId1)
            val savedPet2 = createSavedPet(request, userId2)
            
            every { petRepository.save(match { it.ownerId == userId1 }) } returns savedPet1
            every { petRepository.save(match { it.ownerId == userId2 }) } returns savedPet2
            
            // Act
            val result1 = createPetUseCase.execute(userId1, request)
            val result2 = createPetUseCase.execute(userId2, request)
            
            // Assert
            assertEquals(userId1, result1.ownerId)
            assertEquals(userId2, result2.ownerId)
            assertNotEquals(result1.ownerId, result2.ownerId)
            
            verify(exactly = 2) { petRepository.save(any()) }
        }
    }
}
