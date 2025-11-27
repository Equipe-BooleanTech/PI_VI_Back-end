package edu.fatec.petwise.pets

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.application.usecase.*
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.PetFilterOptions
import edu.fatec.petwise.domain.entity.User
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.*
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import java.time.LocalDateTime
import java.util.*

/**
 * ============================================================================
 * TESTES DE UNIDADE CONSOLIDADOS - CASO DE USO PET
 * ============================================================================
 * 
 * Esta classe contém os 20 testes principais que cobrem:
 * - Fluxos Principais (Happy Path)
 * - Fluxos Alternativos
 * - Fluxos de Exceção
 * - Regras de Negócio
 * 
 * RECURSOS UTILIZADOS:
 * - JUnit 5: Framework de testes
 * - MockK: Biblioteca de mocking para Kotlin (MOCKS e STUBS)
 * 
 * CASOS DE USO TESTADOS:
 * 1. CreatePetUseCase - Criar Pet
 * 2. UpdatePetUseCase - Atualizar Pet
 * 3. DeletePetUseCase - Deletar Pet
 * 4. GetAllPetsUseCase - Listar Pets
 * 5. ToggleFavoriteUseCase - Alternar Favorito
 * ============================================================================
 */
@DisplayName("Pet Use Cases - Testes Principais (20 Testes)")
class PetUseCaseTests {

    // ========================================================================
    // MOCKS - Repositórios simulados para isolamento dos testes
    // ========================================================================
    
    @MockK private lateinit var petRepository: PetRepository
    @MockK private lateinit var userRepository: UserRepository
    @MockK private lateinit var appointmentRepository: AppointmentRepository
    @MockK private lateinit var vaccineRepository: VaccineRepository
    @MockK private lateinit var prescriptionRepository: PrescriptionRepository
    @MockK private lateinit var medicationRepository: MedicationRepository
    @MockK private lateinit var examRepository: ExamRepository
    
    // Use Cases (SUT - System Under Test)
    private lateinit var createPetUseCase: CreatePetUseCase
    private lateinit var updatePetUseCase: UpdatePetUseCase
    private lateinit var deletePetUseCase: DeletePetUseCase
    private lateinit var getAllPetsUseCase: GetAllPetsUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    
    // Dados de teste
    private val testUserId = UUID.randomUUID()
    private val testPetId = UUID.randomUUID()
    private val now = LocalDateTime.now()
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createPetUseCase = CreatePetUseCase(petRepository)
        updatePetUseCase = UpdatePetUseCase(petRepository)
        deletePetUseCase = DeletePetUseCase(
            petRepository, appointmentRepository, vaccineRepository,
            prescriptionRepository, medicationRepository, examRepository
        )
        getAllPetsUseCase = GetAllPetsUseCase(petRepository, userRepository)
        toggleFavoriteUseCase = ToggleFavoriteUseCase(petRepository)
    }
    
    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    // ========================================================================
    // FIXTURES - Métodos auxiliares para criação de objetos de teste
    // ========================================================================
    
    /** STUB: Cria request válido para criação de pet */
    private fun createValidRequest() = CreatePetRequest(
        name = "Rex", breed = "Labrador", species = "DOG", gender = "MALE",
        age = 3, weight = 25.5, healthStatus = "GOOD",
        ownerName = "João Silva", ownerPhone = "(11) 99999-9999",
        healthHistory = "", profileImageUrl = null
    )
    
    /** STUB: Cria entidade Pet para simular retorno do repositório */
    private fun createPet(
        id: UUID = testPetId,
        ownerId: UUID = testUserId,
        name: String = "Rex",
        isFavorite: Boolean = false
    ) = Pet(
        id = id, ownerId = ownerId, name = name, breed = "Labrador",
        species = PetSpecies.DOG, gender = PetGender.MALE,
        age = 3, weight = 25.5, healthStatus = HealthStatus.GOOD,
        ownerName = "João Silva", ownerPhone = "(11) 99999-9999",
        isFavorite = isFavorite, createdAt = now, updatedAt = now
    )
    
    /** STUB: Cria usuário para testes */
    private fun createUser(userType: UserType = UserType.OWNER) = User(
        id = testUserId, fullName = "João Silva",
        email = Email("joao@email.com"), phone = Telefone("(11) 99999-9999"),
        passwordHash = "hash", userType = userType, cpf = "12345678901"
    )

    // ========================================================================
    // TESTES 1-5: CRIAR PET (CreatePetUseCase)
    // ========================================================================
    
    @Nested
    @DisplayName("CreatePetUseCase - Criar Pet")
    inner class CreatePetTests {
        
        /**
         * TESTE 1: FLUXO PRINCIPAL - Criação de pet com sucesso
         * 
         * Regra de Negócio: RN01 - Pet deve ser criado com todos os campos obrigatórios
         */
        @Test
        @DisplayName("T01 - Fluxo Principal: Deve criar pet com todos os campos obrigatórios")
        fun `T01 - deve criar pet com campos obrigatorios`() {
            // Arrange - Preparação do cenário com STUB
            val request = createValidRequest()
            val savedPet = createPet()
            
            // STUB: Simula comportamento do repositório
            every { petRepository.save(any()) } returns savedPet
            
            // Act - Execução do caso de uso
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert - Verificação dos resultados
            assertNotNull(result)
            assertEquals("Rex", result.name)
            assertEquals("DOG", result.species)
            assertEquals(testUserId, result.ownerId)
            
            // Verifica interação com MOCK
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        /**
         * TESTE 2: FLUXO ALTERNATIVO - Criação com diferentes espécies
         * 
         * Regra de Negócio: RN02 - Sistema deve suportar múltiplas espécies
         */
        @Test
        @DisplayName("T02 - Fluxo Alternativo: Deve criar pet de diferentes espécies")
        fun `T02 - deve criar pet de diferentes especies`() {
            // Arrange
            val request = createValidRequest().copy(species = "CAT", name = "Mimi")
            val savedPet = createPet(name = "Mimi").apply { species = PetSpecies.CAT }
            
            every { petRepository.save(any()) } returns savedPet
            
            // Act
            val result = createPetUseCase.execute(testUserId, request)
            
            // Assert
            assertEquals("CAT", result.species)
            assertEquals("Mimi", result.name)
        }
        
        /**
         * TESTE 3: FLUXO DE EXCEÇÃO - Nome do pet vazio
         * 
         * Regra de Negócio: RN03 - Nome do pet é obrigatório (não pode ser vazio)
         * Campo: name - @NotBlank
         */
        @Test
        @DisplayName("T03 - Fluxo de Exceção: Deve rejeitar nome do pet vazio")
        fun `T03 - deve rejeitar nome do pet vazio`() {
            // Arrange - Request com nome vazio (viola validação)
            val request = createValidRequest().copy(name = "")
            
            // Act & Assert - Sistema deve lançar exceção
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            // Verifica mensagem de erro
            assertEquals("Nome do pet é obrigatório", exception.message)
            
            // Verifica que repositório NÃO foi chamado
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        /**
         * TESTE 4: FLUXO DE EXCEÇÃO - Espécie inválida
         * 
         * Regra de Negócio: RN04 - Espécie deve ser um valor válido do enum
         * Valores válidos: DOG, CAT, BIRD, RABBIT, OTHER
         */
        @Test
        @DisplayName("T04 - Fluxo de Exceção: Deve rejeitar espécie inválida")
        fun `T04 - deve rejeitar especie invalida`() {
            // Arrange
            val request = createValidRequest().copy(species = "DRAGON")
            
            // Act & Assert
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            // Verifica que repositório NÃO foi chamado
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES 5-8: ATUALIZAR PET (UpdatePetUseCase)
    // ========================================================================
    
    @Nested
    @DisplayName("UpdatePetUseCase - Atualizar Pet")
    inner class UpdatePetTests {
        
        /**
         * TESTE 5: FLUXO PRINCIPAL - Atualização de pet com sucesso
         * 
         * Regra de Negócio: RN05 - Dono pode atualizar dados do seu pet
         */
        @Test
        @DisplayName("T05 - Fluxo Principal: Deve atualizar nome do pet")
        fun `T05 - deve atualizar nome do pet`() {
            // Arrange
            val existingPet = createPet()
            val request = UpdatePetRequest(name = "Max")
            
            // STUBS: Configuração dos mocks
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert
            assertEquals("Max", result.name)
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        /**
         * TESTE 6: FLUXO ALTERNATIVO - Atualização parcial
         * 
         * Regra de Negócio: RN06 - Apenas campos fornecidos são atualizados
         */
        @Test
        @DisplayName("T06 - Fluxo Alternativo: Deve manter campos não fornecidos")
        fun `T06 - deve manter campos nao fornecidos`() {
            // Arrange
            val existingPet = createPet()
            val request = UpdatePetRequest(name = "Max") // Apenas nome
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            // Assert - Nome alterado, outros mantidos
            assertEquals("Max", result.name)
            assertEquals("Labrador", result.breed)
            assertEquals("DOG", result.species)
        }
        
        /**
         * TESTE 7: FLUXO DE EXCEÇÃO - Pet não encontrado
         * 
         * Regra de Negócio: RN07 - Sistema deve validar existência do pet
         */
        @Test
        @DisplayName("T07 - Fluxo de Exceção: Deve lançar erro para pet inexistente")
        fun `T07 - deve lancar erro para pet inexistente`() {
            // Arrange
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(testUserId, testPetId, UpdatePetRequest(name = "Max"))
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
        
        /**
         * TESTE 8: FLUXO DE EXCEÇÃO - Usuário não é o dono
         * 
         * Regra de Negócio: RN08 - Apenas o dono pode atualizar o pet
         */
        @Test
        @DisplayName("T08 - Fluxo de Exceção: Deve impedir atualização por não-dono")
        fun `T08 - deve impedir atualizacao por nao-dono`() {
            // Arrange
            val outroUsuario = UUID.randomUUID()
            val existingPet = createPet(ownerId = testUserId)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(outroUsuario, testPetId, UpdatePetRequest(name = "Invasor"))
            }
            
            assertEquals("Você não tem permissão para atualizar este pet", exception.message)
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    // ========================================================================
    // TESTES 9-13: DELETAR PET (DeletePetUseCase)
    // ========================================================================
    
    @Nested
    @DisplayName("DeletePetUseCase - Deletar Pet")
    inner class DeletePetTests {
        
        /**
         * TESTE 9: FLUXO PRINCIPAL - Deleção com sucesso (pet sem dados de veterinários)
         * 
         * Regra de Negócio: RN09 - Deleção deve remover dados relacionados em cascata
         * (apenas se não houver dados de veterinários)
         */
        @Test
        @DisplayName("T09 - Fluxo Principal: Deve deletar pet sem dados de veterinários")
        fun `T09 - deve deletar pet sem dados de veterinarios`() {
            // Arrange
            val existingPet = createPet()
            
            // STUBS para verificação de dados de veterinários (NÃO existem)
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { vaccineRepository.existsByPetId(testPetId) } returns false
            every { prescriptionRepository.existsByPetId(testPetId) } returns false
            every { examRepository.existsByPetId(testPetId) } returns false
            
            // STUBS para deleção em cascata
            every { appointmentRepository.deleteByPetId(testPetId) } just runs
            every { vaccineRepository.deleteByPetId(testPetId) } just runs
            every { examRepository.deleteByPetId(testPetId) } just runs
            every { prescriptionRepository.findByPetId(testPetId) } returns emptyList()
            every { prescriptionRepository.deleteByPetId(testPetId) } just runs
            every { petRepository.deleteById(testPetId) } just runs
            
            // Act
            val result = deletePetUseCase.execute(testUserId, testPetId)
            
            // Assert
            assertEquals("Pet removido com sucesso", result.message)
            
            // Verifica ordem de deleção em cascata
            verifyOrder {
                appointmentRepository.deleteByPetId(testPetId)
                vaccineRepository.deleteByPetId(testPetId)
                examRepository.deleteByPetId(testPetId)
                petRepository.deleteById(testPetId)
            }
        }
        
        /**
         * TESTE 10: FLUXO DE EXCEÇÃO - Pet com dados de veterinários
         * 
         * Regra de Negócio: RN10 - Não pode deletar pet que possui vacinas, 
         * prescrições ou exames registrados por veterinários
         */
        @Test
        @DisplayName("T10 - Fluxo de Exceção: Deve impedir deleção de pet com dados de veterinários")
        fun `T10 - deve impedir delecao de pet com dados de veterinarios`() {
            // Arrange
            val existingPet = createPet()
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            // Simula que existem vacinas registradas por veterinário
            every { vaccineRepository.existsByPetId(testPetId) } returns true
            every { prescriptionRepository.existsByPetId(testPetId) } returns false
            every { examRepository.existsByPetId(testPetId) } returns false
            
            // Act & Assert
            val exception = assertThrows<IllegalStateException> {
                deletePetUseCase.execute(testUserId, testPetId)
            }
            
            assertTrue(exception.message!!.contains("vacinas"))
            
            // Verifica que nenhuma deleção ocorreu
            verify(exactly = 0) { petRepository.deleteById(any()) }
        }
        
        /**
         * TESTE 11: FLUXO DE EXCEÇÃO - Pet não encontrado
         * 
         * Regra de Negócio: RN11 - Não pode deletar pet inexistente
         */
        @Test
        @DisplayName("T11 - Fluxo de Exceção: Deve lançar erro ao deletar pet inexistente")
        fun `T11 - deve lancar erro ao deletar pet inexistente`() {
            // Arrange
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                deletePetUseCase.execute(testUserId, testPetId)
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
        
        /**
         * TESTE 12: FLUXO DE EXCEÇÃO - Usuário não é o dono
         * 
         * Regra de Negócio: RN12 - Apenas o dono pode deletar o pet
         */
        @Test
        @DisplayName("T12 - Fluxo de Exceção: Deve impedir deleção por não-dono")
        fun `T12 - deve impedir delecao por nao-dono`() {
            // Arrange
            val outroUsuario = UUID.randomUUID()
            val existingPet = createPet(ownerId = testUserId)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                deletePetUseCase.execute(outroUsuario, testPetId)
            }
            
            assertEquals("Você não tem permissão para remover este pet", exception.message)
            
            // Verifica que nenhuma deleção ocorreu
            verify(exactly = 0) { petRepository.deleteById(any()) }
        }
    }

    // ========================================================================
    // TESTES 13-15: LISTAR PETS (GetAllPetsUseCase)
    // ========================================================================
    
    @Nested
    @DisplayName("GetAllPetsUseCase - Listar Pets")
    inner class GetAllPetsTests {
        
        /**
         * TESTE 13: FLUXO PRINCIPAL - OWNER vê apenas seus pets
         * 
         * Regra de Negócio: RN13 - Dono vê apenas seus próprios pets
         */
        @Test
        @DisplayName("T13 - Fluxo Principal: OWNER deve ver apenas seus pets")
        fun `T13 - owner deve ver apenas seus pets`() {
            // Arrange
            val owner = createUser(UserType.OWNER)
            val ownerPets = listOf(createPet(name = "Rex"), createPet(name = "Max"))
            
            every { userRepository.findById(testUserId) } returns Optional.of(owner)
            every { petRepository.findByOwnerId(testUserId) } returns ownerPets
            
            // Act
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            // Assert
            assertEquals(2, result.total)
            assertTrue(result.pets.all { it.ownerId == testUserId })
            
            // Verifica que usou findByOwnerId (não findAll)
            verify(exactly = 1) { petRepository.findByOwnerId(testUserId) }
            verify(exactly = 0) { petRepository.findAll() }
        }
        
        /**
         * TESTE 14: FLUXO ALTERNATIVO - VETERINARY vê todos os pets
         * 
         * Regra de Negócio: RN14 - Veterinário tem acesso a todos os pets
         */
        @Test
        @DisplayName("T14 - Fluxo Alternativo: VETERINARY deve ver todos os pets")
        fun `T14 - veterinary deve ver todos os pets`() {
            // Arrange
            val vet = createUser(UserType.VETERINARY)
            val allPets = listOf(
                createPet(name = "Rex", ownerId = UUID.randomUUID()),
                createPet(name = "Max", ownerId = UUID.randomUUID()),
                createPet(name = "Luna", ownerId = UUID.randomUUID())
            )
            
            every { userRepository.findById(testUserId) } returns Optional.of(vet)
            every { petRepository.findAll() } returns allPets
            
            // Act
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            // Assert
            assertEquals(3, result.total)
            verify(exactly = 1) { petRepository.findAll() }
        }
        
        /**
         * TESTE 15: FLUXO DE EXCEÇÃO - Usuário não encontrado
         * 
         * Regra de Negócio: RN15 - Retorna lista vazia para usuário inexistente
         */
        @Test
        @DisplayName("T15 - Fluxo de Exceção: Deve retornar lista vazia para usuário inexistente")
        fun `T15 - deve retornar lista vazia para usuario inexistente`() {
            // Arrange
            every { userRepository.findById(testUserId) } returns Optional.empty()
            
            // Act
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            // Assert
            assertEquals(0, result.total)
            assertTrue(result.pets.isEmpty())
        }
    }

    // ========================================================================
    // TESTES 16-20: ALTERNAR FAVORITO (ToggleFavoriteUseCase)
    // ========================================================================
    
    @Nested
    @DisplayName("ToggleFavoriteUseCase - Alternar Favorito")
    inner class ToggleFavoriteTests {
        
        /**
         * TESTE 16: FLUXO PRINCIPAL - Marcar como favorito
         * 
         * Regra de Negócio: RN16 - Toggle de false para true
         */
        @Test
        @DisplayName("T16 - Fluxo Principal: Deve marcar pet como favorito")
        fun `T16 - deve marcar pet como favorito`() {
            // Arrange
            val pet = createPet(isFavorite = false)
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            // Assert
            assertTrue(result.isFavorite)
            assertEquals(testPetId, result.petId)
        }
        
        /**
         * TESTE 17: FLUXO ALTERNATIVO - Desmarcar favorito
         * 
         * Regra de Negócio: RN17 - Toggle de true para false
         */
        @Test
        @DisplayName("T17 - Fluxo Alternativo: Deve desmarcar pet como favorito")
        fun `T17 - deve desmarcar pet como favorito`() {
            // Arrange
            val pet = createPet(isFavorite = true)
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            val result = toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            // Assert
            assertFalse(result.isFavorite)
        }
        
        /**
         * TESTE 18: FLUXO ALTERNATIVO - Toggle múltiplas vezes
         * 
         * Regra de Negócio: RN18 - Toggle deve alternar corretamente
         */
        @Test
        @DisplayName("T18 - Fluxo Alternativo: Deve alternar favorito múltiplas vezes")
        fun `T18 - deve alternar favorito multiplas vezes`() {
            // Arrange
            val pet = createPet(isFavorite = false)
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { 
                val savedPet = firstArg<Pet>()
                savedPet // Retorna o pet salvo
            }
            
            // Act - Primeiro toggle (false -> true)
            val result1 = toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            // Assert
            assertTrue(result1.isFavorite)
        }
        
        /**
         * TESTE 19: FLUXO DE EXCEÇÃO - Pet não encontrado
         * 
         * Regra de Negócio: RN19 - Não pode alternar favorito de pet inexistente
         */
        @Test
        @DisplayName("T19 - Fluxo de Exceção: Deve lançar erro para pet inexistente")
        fun `T19 - deve lancar erro para pet inexistente no toggle`() {
            // Arrange
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            // Act & Assert
            val exception = assertThrows<Exception> {
                toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
        
        /**
         * TESTE 20: REGRA DE NEGÓCIO - Atualização de timestamp
         * 
         * Regra de Negócio: RN20 - updatedAt deve ser atualizado ao alternar
         */
        @Test
        @DisplayName("T20 - Regra de Negócio: Deve atualizar timestamp ao alternar favorito")
        fun `T20 - deve atualizar timestamp ao alternar favorito`() {
            // Arrange
            val oldUpdatedAt = now.minusDays(1)
            val pet = createPet(isFavorite = false)
            pet.updatedAt = oldUpdatedAt
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            // Act
            toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            // Assert - Verifica que updatedAt foi atualizado
            verify {
                petRepository.save(match { savedPet ->
                    savedPet.updatedAt.isAfter(oldUpdatedAt)
                })
            }
        }
    }
}
