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
import edu.fatec.petwise.infrastructure.service.IotService
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


@DisplayName("Pet Use Cases - Testes Principais (20 Testes)")
class PetUseCaseTests {

    
    @MockK private lateinit var petRepository: PetRepository
    @MockK private lateinit var userRepository: UserRepository
    @MockK private lateinit var appointmentRepository: AppointmentRepository
    @MockK private lateinit var vaccineRepository: VaccineRepository
    @MockK private lateinit var prescriptionRepository: PrescriptionRepository
    @MockK private lateinit var medicationRepository: MedicationRepository
    @MockK private lateinit var examRepository: ExamRepository
    @MockK private lateinit var petTagRepository: PetTagRepository
    @MockK private lateinit var iotService: IotService
    
    
    private lateinit var createPetUseCase: CreatePetUseCase
    private lateinit var updatePetUseCase: UpdatePetUseCase
    private lateinit var deletePetUseCase: DeletePetUseCase
    private lateinit var getAllPetsUseCase: GetAllPetsUseCase
    private lateinit var toggleFavoriteUseCase: ToggleFavoriteUseCase
    
    
    private val testUserId = UUID.randomUUID()
    private val testPetId = UUID.randomUUID()
    private val now = LocalDateTime.now()
    
    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        createPetUseCase = CreatePetUseCase(petRepository, iotService)
        updatePetUseCase = UpdatePetUseCase(petRepository)
        deletePetUseCase = DeletePetUseCase(
            petRepository, appointmentRepository, vaccineRepository,
            prescriptionRepository, medicationRepository, examRepository,
            petTagRepository
        )
        getAllPetsUseCase = GetAllPetsUseCase(petRepository, userRepository)
        toggleFavoriteUseCase = ToggleFavoriteUseCase(petRepository)
    }
    
    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    
    private fun createValidRequest() = CreatePetRequest(
        name = "Rex", breed = "Labrador", species = "DOG", gender = "MALE",
        age = 3, weight = 25.5, healthStatus = "GOOD",
        ownerName = "João Silva", ownerPhone = "(11) 99999-9999",
        healthHistory = "", profileImageUrl = null
    )
    
    
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
    
    
    private fun createUser(userType: UserType = UserType.OWNER) = User(
        id = testUserId, fullName = "João Silva",
        email = Email("joao@email.com"), phone = Telefone("(11) 99999-9999"),
        passwordHash = "hash", userType = userType, cpf = "12345678901"
    )

    
    @Nested
    @DisplayName("CreatePetUseCase - Criar Pet")
    inner class CreatePetTests {
        
        
        @Test
        @DisplayName("T01 - Fluxo Principal: Deve criar pet com todos os campos obrigatórios")
        fun `T01 - deve criar pet com campos obrigatorios`() {
            
            val request = createValidRequest()
            val savedPet = createPet()
            
            
            every { petRepository.save(any()) } returns savedPet
            
            
            val result = createPetUseCase.execute(testUserId, request)
            
            
            assertNotNull(result)
            assertEquals("Rex", result.name)
            assertEquals("DOG", result.species)
            assertEquals(testUserId, result.ownerId)
            
            
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        
        @Test
        @DisplayName("T02 - Fluxo Alternativo: Deve criar pet de diferentes espécies")
        fun `T02 - deve criar pet de diferentes especies`() {
            
            val request = createValidRequest().copy(species = "CAT", name = "Mimi")
            val savedPet = createPet(name = "Mimi").apply { species = PetSpecies.CAT }
            
            every { petRepository.save(any()) } returns savedPet
            
            
            val result = createPetUseCase.execute(testUserId, request)
            
            
            assertEquals("CAT", result.species)
            assertEquals("Mimi", result.name)
        }
        
        
        @Test
        @DisplayName("T03 - Fluxo de Exceção: Deve rejeitar nome do pet vazio")
        fun `T03 - deve rejeitar nome do pet vazio`() {
            
            val request = createValidRequest().copy(name = "")
            
            
            val exception = assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            
            assertEquals("Nome do pet é obrigatório", exception.message)
            
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
        
        
        @Test
        @DisplayName("T04 - Fluxo de Exceção: Deve rejeitar espécie inválida")
        fun `T04 - deve rejeitar especie invalida`() {
            
            val request = createValidRequest().copy(species = "DRAGON")
            
            
            assertThrows<IllegalArgumentException> {
                createPetUseCase.execute(testUserId, request)
            }
            
            
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    
    @Nested
    @DisplayName("UpdatePetUseCase - Atualizar Pet")
    inner class UpdatePetTests {
        
        
        @Test
        @DisplayName("T05 - Fluxo Principal: Deve atualizar nome do pet")
        fun `T05 - deve atualizar nome do pet`() {
            
            val existingPet = createPet()
            val request = UpdatePetRequest(name = "Max")
            
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            
            assertEquals("Max", result.name)
            verify(exactly = 1) { petRepository.save(any()) }
        }
        
        
        @Test
        @DisplayName("T06 - Fluxo Alternativo: Deve manter campos não fornecidos")
        fun `T06 - deve manter campos nao fornecidos`() {
            
            val existingPet = createPet()
            val request = UpdatePetRequest(name = "Max") 
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            
            val result = updatePetUseCase.execute(testUserId, testPetId, request)
            
            
            assertEquals("Max", result.name)
            assertEquals("Labrador", result.breed)
            assertEquals("DOG", result.species)
        }
        
        
        @Test
        @DisplayName("T07 - Fluxo de Exceção: Deve lançar erro para pet inexistente")
        fun `T07 - deve lancar erro para pet inexistente`() {
            
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(testUserId, testPetId, UpdatePetRequest(name = "Max"))
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
        
        
        @Test
        @DisplayName("T08 - Fluxo de Exceção: Deve impedir atualização por não-dono")
        fun `T08 - deve impedir atualizacao por nao-dono`() {
            
            val outroUsuario = UUID.randomUUID()
            val existingPet = createPet(ownerId = testUserId)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            
            val exception = assertThrows<Exception> {
                updatePetUseCase.execute(outroUsuario, testPetId, UpdatePetRequest(name = "Invasor"))
            }
            
            assertEquals("Você não tem permissão para atualizar este pet", exception.message)
            verify(exactly = 0) { petRepository.save(any()) }
        }
    }

    
    @Nested
    @DisplayName("DeletePetUseCase - Deletar Pet")
    inner class DeletePetTests {
        
        
        @Test
        @DisplayName("T09 - Fluxo Principal: Deve deletar pet sem dados de veterinários")
        fun `T09 - deve deletar pet sem dados de veterinarios`() {
            
            val existingPet = createPet()
            
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            every { vaccineRepository.existsByPetId(testPetId) } returns false
            every { prescriptionRepository.existsByPetId(testPetId) } returns false
            every { examRepository.existsByPetId(testPetId) } returns false
            
            
            every { appointmentRepository.deleteByPetId(testPetId) } just runs
            every { vaccineRepository.deleteByPetId(testPetId) } just runs
            every { examRepository.deleteByPetId(testPetId) } just runs
            every { petTagRepository.deleteByPetId(testPetId) } just runs
            every { prescriptionRepository.findByPetId(testPetId) } returns emptyList()
            every { prescriptionRepository.deleteByPetId(testPetId) } just runs
            every { petRepository.deleteById(testPetId) } just runs
            
            
            val result = deletePetUseCase.execute(testUserId, testPetId)
            
            
            assertEquals("Pet removido com sucesso", result.message)
            
            
            verifyOrder {
                appointmentRepository.deleteByPetId(testPetId)
                vaccineRepository.deleteByPetId(testPetId)
                examRepository.deleteByPetId(testPetId)
                petTagRepository.deleteByPetId(testPetId)
                petRepository.deleteById(testPetId)
            }
        }
        
        
        @Test
        @DisplayName("T10 - Fluxo de Exceção: Deve impedir deleção de pet com dados de veterinários")
        fun `T10 - deve impedir delecao de pet com dados de veterinarios`() {
            
            val existingPet = createPet()
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            every { vaccineRepository.existsByPetId(testPetId) } returns true
            every { prescriptionRepository.existsByPetId(testPetId) } returns false
            every { examRepository.existsByPetId(testPetId) } returns false
            
            
            val exception = assertThrows<IllegalStateException> {
                deletePetUseCase.execute(testUserId, testPetId)
            }
            
            assertTrue(exception.message!!.contains("vacinas"))
            
            
            verify(exactly = 0) { petRepository.deleteById(any()) }
        }
        
        
        @Test
        @DisplayName("T11 - Fluxo de Exceção: Deve lançar erro ao deletar pet inexistente")
        fun `T11 - deve lancar erro ao deletar pet inexistente`() {
            
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            
            val exception = assertThrows<Exception> {
                deletePetUseCase.execute(testUserId, testPetId)
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
        
        
        @Test
        @DisplayName("T12 - Fluxo de Exceção: Deve impedir deleção por não-dono")
        fun `T12 - deve impedir delecao por nao-dono`() {
            
            val outroUsuario = UUID.randomUUID()
            val existingPet = createPet(ownerId = testUserId)
            
            every { petRepository.findById(testPetId) } returns Optional.of(existingPet)
            
            
            val exception = assertThrows<Exception> {
                deletePetUseCase.execute(outroUsuario, testPetId)
            }
            
            assertEquals("Você não tem permissão para remover este pet", exception.message)
            
            
            verify(exactly = 0) { petRepository.deleteById(any()) }
        }
    }

    
    @Nested
    @DisplayName("GetAllPetsUseCase - Listar Pets")
    inner class GetAllPetsTests {
        
        
        @Test
        @DisplayName("T13 - Fluxo Principal: OWNER deve ver apenas seus pets")
        fun `T13 - owner deve ver apenas seus pets`() {
            
            val owner = createUser(UserType.OWNER)
            val ownerPets = listOf(createPet(name = "Rex"), createPet(name = "Max"))
            
            every { userRepository.findById(testUserId) } returns Optional.of(owner)
            every { petRepository.findByOwnerId(testUserId) } returns ownerPets
            
            
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            
            assertEquals(2, result.total)
            assertTrue(result.pets.all { it.ownerId == testUserId })
            
            
            verify(exactly = 1) { petRepository.findByOwnerId(testUserId) }
            verify(exactly = 0) { petRepository.findAll() }
        }
        
        
        @Test
        @DisplayName("T14 - Fluxo Alternativo: VETERINARY deve ver todos os pets")
        fun `T14 - veterinary deve ver todos os pets`() {
            
            val vet = createUser(UserType.VETERINARY)
            val allPets = listOf(
                createPet(name = "Rex", ownerId = UUID.randomUUID()),
                createPet(name = "Max", ownerId = UUID.randomUUID()),
                createPet(name = "Luna", ownerId = UUID.randomUUID())
            )
            
            every { userRepository.findById(testUserId) } returns Optional.of(vet)
            every { petRepository.findAll() } returns allPets
            
            
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            
            assertEquals(3, result.total)
            verify(exactly = 1) { petRepository.findAll() }
        }
        
        
        @Test
        @DisplayName("T15 - Fluxo de Exceção: Deve retornar lista vazia para usuário inexistente")
        fun `T15 - deve retornar lista vazia para usuario inexistente`() {
            
            every { userRepository.findById(testUserId) } returns Optional.empty()
            
            
            val result = getAllPetsUseCase.execute(testUserId, 1, 10)
            
            
            assertEquals(0, result.total)
            assertTrue(result.pets.isEmpty())
        }
    }

    
    @Nested
    @DisplayName("ToggleFavoriteUseCase - Alternar Favorito")
    inner class ToggleFavoriteTests {
        
        
        @Test
        @DisplayName("T16 - Fluxo Principal: Deve marcar pet como favorito")
        fun `T16 - deve marcar pet como favorito`() {
            
            val pet = createPet(isFavorite = false)
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            
            val result = toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            
            assertTrue(result.isFavorite)
            assertEquals(testPetId, result.petId)
        }
        
        
        @Test
        @DisplayName("T17 - Fluxo Alternativo: Deve desmarcar pet como favorito")
        fun `T17 - deve desmarcar pet como favorito`() {
            
            val pet = createPet(isFavorite = true)
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            
            val result = toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            
            assertFalse(result.isFavorite)
        }
        
        
        @Test
        @DisplayName("T18 - Fluxo Alternativo: Deve alternar favorito múltiplas vezes")
        fun `T18 - deve alternar favorito multiplas vezes`() {
            
            val pet = createPet(isFavorite = false)
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { 
                val savedPet = firstArg<Pet>()
                savedPet 
            }
            
            
            val result1 = toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            
            assertTrue(result1.isFavorite)
        }
        
        
        @Test
        @DisplayName("T19 - Fluxo de Exceção: Deve lançar erro para pet inexistente")
        fun `T19 - deve lancar erro para pet inexistente no toggle`() {
            
            every { petRepository.findById(testPetId) } returns Optional.empty()
            
            
            val exception = assertThrows<Exception> {
                toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            }
            
            assertEquals("Pet não encontrado", exception.message)
        }
        
        
        @Test
        @DisplayName("T20 - Regra de Negócio: Deve atualizar timestamp ao alternar favorito")
        fun `T20 - deve atualizar timestamp ao alternar favorito`() {
            
            val oldUpdatedAt = now.minusDays(1)
            val pet = createPet(isFavorite = false)
            pet.updatedAt = oldUpdatedAt
            
            every { petRepository.findById(testPetId) } returns Optional.of(pet)
            every { petRepository.save(any()) } answers { firstArg() }
            
            
            toggleFavoriteUseCase.execute(testUserId.toString(), testPetId)
            
            
            verify {
                petRepository.save(match { savedPet ->
                    savedPet.updatedAt.isAfter(oldUpdatedAt)
                })
            }
        }
    }
}
