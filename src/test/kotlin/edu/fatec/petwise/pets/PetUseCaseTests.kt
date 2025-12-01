package edu.fatec.petwise.pets

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.usecase.*
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.entity.User
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.*
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import edu.fatec.petwise.infrastructure.service.IotService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertNotNull
import java.time.LocalDateTime
import java.util.*

/**
 * =====================================================================
 * TESTES UNITÁRIOS - Casos de Uso de Pet (Cadastro e Listagem)
 * =====================================================================
 * 
 * Este arquivo contém testes complementares ao CreatePetUseCaseTest.kt
 * focando em operações relacionadas ao cadastro:
 * 
 * - GetAllPetsUseCase: Listagem de pets cadastrados
 * 
 * Os testes de CreatePetUseCase estão em:
 * edu.fatec.petwise.pets.unit.CreatePetUseCaseTest (13 testes)
 * 
 * =====================================================================
 * REGRAS DE NEGÓCIO VALIDADAS
 * =====================================================================
 * 
 * RN01 — Associação de Dono:
 *   Um usuário (tutor) deve poder cadastrar e gerenciar vários pets.
 *   → Testado em: T01 (OWNER vê apenas seus pets)
 * 
 * RN10 — Propriedade de dados:
 *   Usuários só podem visualizar, editar ou excluir dados referentes
 *   às suas próprias entidades.
 *   → Testado em: T01, T02
 * 
 * RN11 — Permissões por perfil:
 *   O sistema deve aplicar permissões conforme o perfil do usuário.
 *   → Testado em: T02 (VETERINARY vê todos os pets)
 * 
 * =====================================================================
 * PADRÕES DE TESTE UTILIZADOS
 * =====================================================================
 * 
 * - MOCK: Repositórios mockados para isolamento
 * - STUB: Respostas pré-definidas para consultas
 * - FIXTURE: Métodos auxiliares para criar dados de teste
 * 
 * @author Equipe PetWise - BooleanTech
 * @version 1.0
 */
@DisplayName("Pet Use Cases - Testes de Cadastro e Listagem")
class PetUseCaseTests {

    // =====================================================================
    // MOCKS - Dependências mockadas
    // =====================================================================
    
    @MockK private lateinit var petRepository: PetRepository
    @MockK private lateinit var userRepository: UserRepository
    @MockK private lateinit var iotService: IotService
    
    // Use Cases sob teste
    private lateinit var createPetUseCase: CreatePetUseCase
    private lateinit var getAllPetsUseCase: GetAllPetsUseCase
    
    // Fixtures
    private val testUserId = UUID.randomUUID()
    private val testPetId = UUID.randomUUID()
    private val now = LocalDateTime.now()
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createPetUseCase = CreatePetUseCase(petRepository, iotService)
        getAllPetsUseCase = GetAllPetsUseCase(petRepository, userRepository)
    }
    
    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    // =====================================================================
    // FIXTURES - Métodos auxiliares para criar dados de teste
    // =====================================================================
    
    /**
     * FIXTURE: Cria uma requisição válida para criação de pet
     */
    private fun createValidRequest() = CreatePetRequest(
        name = "Rex", breed = "Labrador", species = "DOG", gender = "MALE",
        age = 3, weight = 25.5, healthStatus = "GOOD",
        ownerName = "João Silva", ownerPhone = "(11) 99999-9999",
        healthHistory = "", profileImageUrl = null
    )
    
    /**
     * FIXTURE: Cria uma entidade Pet
     */
    private fun createPet(
        id: UUID = testPetId,
        ownerId: UUID = testUserId,
        name: String = "Rex"
    ) = Pet(
        id = id, ownerId = ownerId, name = name, breed = "Labrador",
        species = PetSpecies.DOG, gender = PetGender.MALE,
        age = 3, weight = 25.5, healthStatus = HealthStatus.GOOD,
        ownerName = "João Silva", ownerPhone = "(11) 99999-9999",
        createdAt = now, updatedAt = now
    )
    
    /**
     * FIXTURE: Cria um usuário
     */
    private fun createUser(userType: UserType = UserType.OWNER) = User(
        id = testUserId, fullName = "João Silva",
        email = Email("joao@email.com"), phone = Telefone("(11) 99999-9999"),
        passwordHash = "hash", userType = userType, cpf = "12345678901"
    )

    // =====================================================================
    // NOTA: Testes de CreatePetUseCase
    // Movidos para: edu.fatec.petwise.pets.unit.CreatePetUseCaseTest
    // Contém 13 casos de teste seguindo o Grafo de Estados (GE)
    // =====================================================================

    // =====================================================================
    // GetAllPetsUseCase - Listar Pets Cadastrados
    // =====================================================================
    
    @Nested
    @DisplayName("GetAllPetsUseCase - Listar Pets Cadastrados")
    inner class GetAllPetsTests {
        
        /**
         * T01: OWNER deve ver apenas seus próprios pets cadastrados
         * 
         * Regras validadas:
         * - RN01: Associação de Dono (pets vinculados ao usuário)
         * - RN10: Propriedade de dados (só vê seus dados)
         */
        @Test
        @DisplayName("T01 - Fluxo Principal: OWNER deve ver apenas seus pets cadastrados")
        fun `T01 - owner deve ver apenas seus pets`() {
            // Arrange - Usuário OWNER com pets cadastrados
            val owner = createUser(UserType.OWNER)
            val ownerPets = listOf(
                createPet(id = UUID.randomUUID(), name = "Rex"),
                createPet(id = UUID.randomUUID(), name = "Max")
            )
            
            // STUB: Simula retorno do repositório
            every { userRepository.findById(testUserId) } returns Optional.of(owner)
            every { petRepository.findByOwnerId(testUserId) } returns ownerPets
            
            // Act - Listar pets do usuário
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            // Assert - Deve retornar apenas os pets do usuário
            assertEquals(2, result.total, "Deve retornar 2 pets")
            assertTrue(result.pets.all { it.ownerId == testUserId }, "Todos devem pertencer ao usuário")
            
            // Verificar que buscou apenas por ownerId, não todos
            verify(exactly = 1) { petRepository.findByOwnerId(testUserId) }
            verify(exactly = 0) { petRepository.findAll() }
        }
        
        /**
         * T02: VETERINARY deve ver todos os pets cadastrados no sistema
         * 
         * Regras validadas:
         * - RN11: Permissões por perfil (veterinário tem acesso amplo)
         */
        @Test
        @DisplayName("T02 - Fluxo Alternativo: VETERINARY deve ver todos os pets")
        fun `T02 - veterinary deve ver todos os pets`() {
            // Arrange - Usuário VETERINARY
            val vet = createUser(UserType.VETERINARY)
            val allPets = listOf(
                createPet(id = UUID.randomUUID(), name = "Rex", ownerId = UUID.randomUUID()),
                createPet(id = UUID.randomUUID(), name = "Max", ownerId = UUID.randomUUID()),
                createPet(id = UUID.randomUUID(), name = "Luna", ownerId = UUID.randomUUID())
            )
            
            // STUB: Retorna todos os pets
            every { userRepository.findById(testUserId) } returns Optional.of(vet)
            every { petRepository.findAll() } returns allPets
            
            // Act
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            // Assert - Deve ver todos os pets de todos os usuários
            assertEquals(3, result.total, "Veterinário deve ver todos os 3 pets")
            verify(exactly = 1) { petRepository.findAll() }
        }
        
        /**
         * T03: Deve retornar lista vazia para usuário inexistente
         * 
         * Testa tratamento de erro quando usuário não existe
         */
        @Test
        @DisplayName("T03 - Fluxo de Exceção: Lista vazia para usuário inexistente")
        fun `T03 - deve retornar lista vazia para usuario inexistente`() {
            // Arrange - Usuário não existe
            every { userRepository.findById(testUserId) } returns Optional.empty()
            
            // Act
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            // Assert - Lista vazia, sem erro
            assertEquals(0, result.total)
            assertTrue(result.pets.isEmpty())
        }
    }
}
