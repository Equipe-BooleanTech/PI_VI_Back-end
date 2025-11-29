package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.enums.HealthStatus
import edu.fatec.petwise.domain.enums.PetGender
import edu.fatec.petwise.domain.enums.PetSpecies
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.infrastructure.service.IotService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreatePetUseCase(
    private val petRepository: PetRepository,
    private val iotService: IotService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, request: CreatePetRequest): PetResponse {
        // Validação de campos obrigatórios
        validateRequiredFields(request)
        
        // Criar entidade Pet
        val pet = Pet(
            id = null,
            ownerId = userId,
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

        val savedPet = petRepository.save(pet)

        logger.info("Pet criado com sucesso: ID=${savedPet.id}, Nome=${savedPet.name}, Dono=$userId")

        var nfcStatus = "NOT_PAIRED"
        if (request.shouldPairNfc) {
            if (request.readerId.isNullOrBlank()) {
                logger.warn("Reader ID não fornecido para pareamento NFC do pet ${savedPet.id}")
                nfcStatus = "ERROR_NO_READER"
            } else {
                try {
                    iotService.startPairingMode(savedPet.id!!, request.readerId)
                    nfcStatus = "WAITING_TAG"
                    logger.info("Modo de pareamento NFC ativado para pet ${savedPet.id} no leitor ${request.readerId}")
                } catch (e: Exception) {
                    logger.error("Erro ao ativar pareamento NFC: ${e.message}", e)
                    nfcStatus = "ERROR"
                }
            }
        }

        return PetResponse.fromEntity(savedPet).copy(
            hasNfcTag = false,
            nfcPairingStatus = nfcStatus
        )
    }
}
    
    /**
     * Valida campos obrigatórios do request.
     * Lança IllegalArgumentException se algum campo obrigatório estiver vazio.
     */
    private fun validateRequiredFields(request: CreatePetRequest) {
        if (request.name.isBlank()) {
            throw IllegalArgumentException("Nome do pet é obrigatório")
        }
        if (request.breed.isBlank()) {
            throw IllegalArgumentException("Raça é obrigatória")
        }
        if (request.species.isBlank()) {
            throw IllegalArgumentException("Espécie é obrigatória")
        }
        if (request.gender.isBlank()) {
            throw IllegalArgumentException("Gênero é obrigatório")
        }
        if (request.healthStatus.isBlank()) {
            throw IllegalArgumentException("Status de saúde é obrigatório")
        }
        if (request.ownerName.isBlank()) {
            throw IllegalArgumentException("Nome do dono é obrigatório")
        }
        if (request.ownerPhone.isBlank()) {
            throw IllegalArgumentException("Telefone do dono é obrigatório")
        }
        if (request.age < 0) {
            throw IllegalArgumentException("Idade deve ser maior ou igual a 0")
        }
        if (request.weight <= 0) {
            throw IllegalArgumentException("Peso deve ser um valor positivo")
        }
    }
