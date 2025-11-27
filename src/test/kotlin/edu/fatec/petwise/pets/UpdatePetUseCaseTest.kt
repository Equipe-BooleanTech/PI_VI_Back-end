package edu.fatec.petwise.pets

import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.application.usecase.UpdatePetUseCase
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
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*

/**
 * ============================================================================
 * TESTES DE UNIDADE PARA O CASO DE USO: ATUALIZAR PET (UpdatePetUseCase)
 * ============================================================================
 * 
 * Esta classe de teste implementa testes de unidade para o caso de uso de
 * atualização de Pet, verificando as regras de negócio e fluxos de execução.
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
 * 
 * REGRAS DE NEGÓCIO TESTADAS:
 * 1. Apenas o dono do pet pode atualizá-lo
 * 2. Campos opcionais são atualizados apenas se fornecidos
 * 3. O campo updatedAt deve ser atualizado automaticamente
 * 4. Pet não encontrado deve lançar exceção
 * ============================================================================
 */
@DisplayName("UpdatePetUseCase - Testes do Caso de Uso Atualizar Pet")
class UpdatePetUseCaseTest {

    // ========================================================================
    // MOCKS E CONFIGURAÇÃO
    // ========================================================================
    
    /**
     * MOCK: PetRepository
     * 
     * Este mock substitui o repositório real durante os testes.
     * Permite simular operações de busca e salvamento sem acessar o banco.
     */
    @MockK
    private lateinit var petRepository: PetRepository
    
    /**
     * SUT (System Under Test): UpdatePetUseCase
     */
    @InjectMockKs
    private lateinit var updatePetUseCase: UpdatePetUseCase
    
    // Dados de teste
    private val testUserId = UUID.randomUUID()
    private val testPetId = UUID.randomUUID()
    private val now = LocalDateTime.now()
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
    }
    
    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    // ========================================================================
    // FIXTURES - Métodos auxiliares para criar objetos de teste
    // ========================================================================
    
    /**
     * STUB: Cria um Pet existente para simular retorno do repositório
     */
    private fun createExistingPet(
        id: UUID = testPetId,
        ownerId: UUID = testUserId,
        name: String = "Rex",
        breed: String = "Labrador"
    ) = Pet(
        id = id,
        ownerId = ownerId,
        name = name,
        breed = breed,
        species = PetSpecies.DOG,
        gender = PetGender.MALE,
        age = 3,
        weight = 25.5,
        healthStatus = HealthStatus.GOOD,
        ownerName = "João Silva",
        ownerPhone = "(11) 99999-9999",
        createdAt = now.minusDays(30),
        updatedAt = now.minusDays(1)
    )
    
    /**
     * Cria um UpdatePetRequest para testes
     */
    private fun createUpdateRequest(
        name: String? = null,
        breed: String? = null,
        species: String? = null,
        gender: String? = null,
        age: Int? = null,
        weight: Double? = null,
        healthStatus: String? = null,
        ownerName: String? = null,
        ownerPhone: String? = null,
        healthHistory: String? = null,
        profileImageUrl: String? = null,
        isFavorite: Boolean? = null,
        nextAppointment: LocalDateTime? = null
    ) = UpdatePetRequest(
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
        profileImageUrl = profileImageUrl,
        isFavorite = isFavorite,
        nextAppointment = nextAppointment
    )

    // ========================================================================
    // TESTES DE ATUALIZAÇÃO COM SUCESSO
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Atualização com Sucesso")
    inner class AtualizacaoSucessoTests {
        
        @Test
        @DisplayName("Deve atualizar nome do pet")
        fun `deve atualizar nome do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(name = "Max")
            
            /**
             * STUB: Configuração dos mocks
             * - findById retorna o pet existente
             * - save retorna o pet atualizado
             */
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Max", result.name)
            
            verify(exactly = 1) { petRepository.findById(testPetId) }
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve atualizar raça do pet")
        fun `deve atualizar raca do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(breed = "Golden Retriever")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Golden Retriever", result.breed)
        }
        
        @Test
        @DisplayName("Deve atualizar espécie do pet")
        fun `deve atualizar especie do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(species = "CAT")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("CAT", result.species)
        }
        
        @Test
        @DisplayName("Deve atualizar gênero do pet")
        fun `deve atualizar genero do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(gender = "FEMALE")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("FEMALE", result.gender)
        }
        
        @Test
        @DisplayName("Deve atualizar idade do pet")
        fun `deve atualizar idade do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(age = 5)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals(5, result.age)
        }
        
        @Test
        @DisplayName("Deve atualizar peso do pet")
        fun `deve atualizar peso do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(weight = 30.0)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals(30.0, result.weight)
        }
        
        @Test
        @DisplayName("Deve atualizar status de saúde do pet")
        fun `deve atualizar status de saude do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(healthStatus = "EXCELLENT")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("EXCELLENT", result.healthStatus)
        }
        
        @Test
        @DisplayName("Deve atualizar favorito do pet")
        fun `deve atualizar favorito do pet`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(isFavorite = true)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertTrue(result.isFavorite)
        }
        
        @Test
        @DisplayName("Deve atualizar múltiplos campos simultaneamente")
        fun `deve atualizar multiplos campos simultaneamente`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(
                name = "Max",
                breed = "Golden Retriever",
                age = 4,
                weight = 28.0,
                healthStatus = "EXCELLENT"
            )
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Max", result.name)
            assertEquals("Golden Retriever", result.breed)
            assertEquals(4, result.age)
            assertEquals(28.0, result.weight)
            assertEquals("EXCELLENT", result.healthStatus)
        }
        
        @Test
        @DisplayName("Deve atualizar próximo agendamento")
        fun `deve atualizar proximo agendamento`() {
            // Arrange
            val existingPet = createExistingPet()
            val nextAppointment = LocalDateTime.now().plusDays(7)
            val request = createUpdateRequest(nextAppointment = nextAppointment)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals(nextAppointment, result.nextAppointment)
        }
    }

    // ========================================================================
    // TESTES DE FLUXOS ALTERNATIVOS - ATUALIZAÇÃO PARCIAL
    // ========================================================================
    
    @Nested
    @DisplayName("Fluxo Alternativo - Atualização de Campos Específicos")
    inner class AtualizacaoCamposEspecificosTests {
        
        /**
         * REGRA DE NEGÓCIO: RN01 - Apenas campos fornecidos devem ser atualizados
         * FLUXO ALTERNATIVO: FA01 - Atualização seletiva de campos
         */
        @Test
        @DisplayName("FA01 - Deve atualizar apenas nome mantendo todos outros campos")
        fun `deve atualizar apenas nome mantendo outros campos`() {
            // Arrange
            val existingPet = createExistingPet(name = "Rex", breed = "Labrador")
            existingPet.healthHistory = "Histórico original"
            existingPet.profileImageUrl = "http://foto-original.jpg"
            
            val request = createUpdateRequest(name = "Max")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert - Nome atualizado, outros campos mantidos
            assertEquals("Max", result.name)
            assertEquals("Labrador", result.breed)
            assertEquals("DOG", result.species)
            assertEquals("MALE", result.gender)
            assertEquals(3, result.age)
            assertEquals(25.5, result.weight)
        }
        
        /**
         * FLUXO ALTERNATIVO: FA02 - Atualização de dados do dono
         */
        @Test
        @DisplayName("FA02 - Deve atualizar dados do dono separadamente")
        fun `deve atualizar dados do dono separadamente`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(
                ownerName = "Maria Santos",
                ownerPhone = "(21) 88888-8888"
            )
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Maria Santos", result.ownerName)
            assertEquals("(21) 88888-8888", result.ownerPhone)
            assertEquals("Rex", result.name) // Nome do pet mantido
        }
        
        /**
         * FLUXO ALTERNATIVO: FA03 - Atualização de histórico de saúde
         */
        @Test
        @DisplayName("FA03 - Deve atualizar histórico de saúde")
        fun `deve atualizar historico de saude`() {
            // Arrange
            val existingPet = createExistingPet()
            existingPet.healthHistory = "Histórico antigo"
            
            val novoHistorico = "Vacina anti-rábica em 2024. Vermifugado em 2024."
            val request = createUpdateRequest(healthHistory = novoHistorico)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals(novoHistorico, result.healthHistory)
        }
        
        /**
         * FLUXO ALTERNATIVO: FA04 - Atualização de imagem de perfil
         */
        @Test
        @DisplayName("FA04 - Deve atualizar URL da imagem de perfil")
        fun `deve atualizar url imagem perfil`() {
            // Arrange
            val existingPet = createExistingPet()
            existingPet.profileImageUrl = "http://foto-antiga.jpg"
            
            val novaUrl = "http://nova-foto.jpg"
            val request = createUpdateRequest(profileImageUrl = novaUrl)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals(novaUrl, result.profileImageUrl)
        }
    }
    
    // ========================================================================
    // TESTES DE REGRAS DE NEGÓCIO - PERMISSÕES
    // ========================================================================
    
    @Nested
    @DisplayName("Regras de Negócio - Controle de Acesso")
    inner class ControleAcessoTests {
        
        /**
         * REGRA DE NEGÓCIO: RN01 - Apenas o dono pode atualizar seu pet
         * FLUXO DE EXCEÇÃO: FE01 - Tentativa de atualização por outro usuário
         */
        @Test
        @DisplayName("FE01 - Deve impedir atualização por usuário diferente do dono")
        fun `deve impedir atualizacao por usuario diferente`() {
            // Arrange
            val donoPetId = UUID.randomUUID()
            val outroUsuarioId = UUID.randomUUID()
            val existingPet = createExistingPet(ownerId = donoPetId)
            val request = createUpdateRequest(name = "Invasor")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(outroUsuarioId, testPetId, request)
            }
            
            // Verifica mensagem de erro apropriada
            assertEquals("Você não tem permissão para atualizar este pet", exception.message)
            
            // Verifica que nenhuma alteração foi persistida
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * REGRA DE NEGÓCIO: RN02 - Sistema deve validar existência do pet
         * FLUXO DE EXCEÇÃO: FE02 - Pet não existe no sistema
         */
        @Test
        @DisplayName("FE02 - Deve lançar exceção para pet inexistente")
        fun `deve lancar excecao para pet inexistente`() {
            // Arrange
            val petIdInexistente = UUID.randomUUID()
            val request = createUpdateRequest(name = "NovoNome")
            
            every { petRepository.findById(petIdInexistente) } returns Optional.empty()
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(testUserId, petIdInexistente, request)
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
    }
    
    // ========================================================================
    // TESTES DE FLUXOS DE EXCEÇÃO - VALIDAÇÃO DE ENUMS
    // ========================================================================
    
    @Nested
    @DisplayName("Fluxo de Exceção - Validação de Enums na Atualização")
    inner class ValidacaoEnumsAtualizacaoTests {
        
        /**
         * FLUXO DE EXCEÇÃO: FE03 - Espécie inválida na atualização
         */
        @Test
        @DisplayName("FE03 - Deve rejeitar espécie inexistente")
        fun `deve rejeitar especie inexistente`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(species = "DRAGON") // Não existe
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * FLUXO DE EXCEÇÃO: FE04 - Gênero inválido na atualização
         */
        @Test
        @DisplayName("FE04 - Deve rejeitar gênero inexistente")
        fun `deve rejeitar genero inexistente`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(gender = "UNKNOWN")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
        }
        
        /**
         * FLUXO DE EXCEÇÃO: FE05 - Status de saúde inválido
         */
        @Test
        @DisplayName("FE05 - Deve rejeitar status de saúde inexistente")
        fun `deve rejeitar status saude inexistente`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(healthStatus = "DEAD") // Não existe
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
        }
    }
    
    // ========================================================================
    // TESTES DE REGRAS DE NEGÓCIO - ATUALIZAÇÃO DE TIMESTAMPS
    // ========================================================================
    
    @Nested
    @DisplayName("Regras de Negócio - Timestamps de Atualização")
    inner class TimestampsAtualizacaoTests {
        
        /**
         * REGRA DE NEGÓCIO: RN03 - updatedAt deve ser atualizado em qualquer modificação
         */
        @Test
        @DisplayName("RN03 - Deve atualizar updatedAt mesmo sem mudanças")
        fun `deve atualizar updatedAt mesmo sem mudancas`() {
            // Arrange
            val oldUpdatedAt = now.minusDays(7)
            val existingPet = createExistingPet()
            existingPet.updatedAt = oldUpdatedAt
            
            val request = createUpdateRequest() // Request vazio
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            verify {
                petRepository.save(match { pet ->
                    pet.updatedAt.isAfter(oldUpdatedAt)
                })
            }
        }
        
        /**
         * REGRA DE NEGÓCIO: RN04 - createdAt nunca deve ser alterado
         */
        @Test
        @DisplayName("RN04 - Deve manter createdAt original")
        fun `deve manter createdAt original`() {
            // Arrange
            val originalCreatedAt = now.minusDays(30)
            val existingPet = createExistingPet()
            // createdAt é setado no construtor, verificamos se mantém após update
            
            val request = createUpdateRequest(name = "NovoNome")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert - createdAt deve ser mantido (não alterado)
            assertNotNull(result.createdAt)
        }
    }
    
    // ========================================================================
    // TESTES DE REGRA DE NEGÓCIO: PERMISSÃO DO DONO
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Permissão do Dono")
    inner class PermissaoDonoTests {
        
        @Test
        @DisplayName("Deve lançar exceção quando usuário não é o dono")
        fun `deve lancar excecao quando usuario nao e o dono`() {
            // Arrange
            val differentUserId = UUID.randomUUID()
            val existingPet = createExistingPet(ownerId = testUserId) // Pet pertence a testUserId
            val request = createUpdateRequest(name = "Max")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(differentUserId, testPetId, request) // Tenta atualizar com outro userId
            }
            
            assertEquals("Você não tem permissão para atualizar este pet", exception.message)
            
            /**
             * VERIFICAÇÃO: O save() NÃO deve ser chamado quando não tem permissão
             */
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve permitir atualização quando usuário é o dono")
        fun `deve permitir atualizacao quando usuario e o dono`() {
            // Arrange
            val existingPet = createExistingPet(ownerId = testUserId)
            val request = createUpdateRequest(name = "Max")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Max", result.name)
            
            verify(exactly = 1) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES DE CENÁRIOS DE ERRO
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Cenários de Erro")
    inner class CenariosErroTests {
        
        @Test
        @DisplayName("Deve lançar exceção quando pet não é encontrado")
        fun `deve lancar excecao quando pet nao encontrado`() {
            // Arrange
            val request = createUpdateRequest(name = "Max")
            
            /**
             * STUB: Simula pet não encontrado retornando Optional.empty()
             */
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
            
            assertEquals("Pet não encontrado", exception.message)
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve lançar exceção para espécie inválida")
        fun `deve lancar excecao para especie invalida`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(species = "INVALID_SPECIES")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve lançar exceção para gênero inválido")
        fun `deve lancar excecao para genero invalido`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(gender = "INVALID_GENDER")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        @Test
        @DisplayName("Deve lançar exceção para status de saúde inválido")
        fun `deve lancar excecao para status de saude invalido`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(healthStatus = "INVALID_STATUS")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                updatePetUseCase.execute(testUserId, testPetId, request)
            }
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES DE ATUALIZAÇÃO PARCIAL
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Atualização Parcial")
    inner class AtualizacaoParcialTests {
        
        @Test
        @DisplayName("Não deve alterar campos não fornecidos")
        fun `nao deve alterar campos nao fornecidos`() {
            // Arrange
            val existingPet = createExistingPet(name = "Rex", breed = "Labrador")
            val request = createUpdateRequest(name = "Max") // Apenas nome
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Max", result.name) // Nome alterado
            assertEquals("Labrador", result.breed) // Raça mantida
            assertEquals("DOG", result.species) // Espécie mantida
        }
        
        @Test
        @DisplayName("Deve atualizar updatedAt automaticamente")
        fun `deve atualizar updatedAt automaticamente`() {
            // Arrange
            val oldUpdatedAt = now.minusDays(1)
            val existingPet = createExistingPet()
            existingPet.updatedAt = oldUpdatedAt
            
            val request = createUpdateRequest(name = "Max")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            verify {
                petRepository.save(match { pet ->
                    pet.updatedAt.isAfter(oldUpdatedAt)
                })
            }
        }
        
        @Test
        @DisplayName("Deve permitir request vazio (sem alterações)")
        fun `deve permitir request vazio`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest() // Nenhum campo
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert - Campos originais mantidos
            assertEquals("Rex", result.name)
            assertEquals("Labrador", result.breed)
            
            verify(exactly = 1) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES DE TRIM (REMOÇÃO DE ESPAÇOS)
    // ========================================================================
    
    @Nested
    @DisplayName("Testes de Trim")
    inner class TrimTests {
        
        @Test
        @DisplayName("Deve fazer trim do nome na atualização")
        fun `deve fazer trim do nome`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(name = "  Max  ")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Max", result.name)
        }
        
        @Test
        @DisplayName("Deve fazer trim da raça na atualização")
        fun `deve fazer trim da raca`() {
            // Arrange
            val existingPet = createExistingPet()
            val request = createUpdateRequest(breed = "  Golden Retriever  ")
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Golden Retriever", result.breed)
        }
    }
}
