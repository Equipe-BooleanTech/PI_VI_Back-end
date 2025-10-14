package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdateHealthStatusRequest
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreatePetUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: CreatePetRequest, ownerId: UUID): PetResponse {
        logger.info("Criando novo pet: ${request.name}")

        val owner = userRepository.findById(ownerId)
            ?: throw EntityNotFoundException("Usuário", ownerId)

        if (!owner.isOwner()) {
            throw BusinessRuleException("Apenas usuários donos podem criar pets")
        }

        val pet = Pet.create(
            name = request.name,
            species = request.species,
            breed = request.breed,
            birthDate = request.birthDate,
            weight = request.weight,
            ownerId = ownerId,
            healthStatus = request.healthStatus
        )

        val savedPet = petRepository.save(pet)
        logger.info("Pet criado com sucesso. ID: ${savedPet.id}")
        
        return savedPet.toResponse()
    }
}

@Service
class GetPetByIdUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): PetResponse {
        logger.info("Buscando pet por ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar este pet")
        }
        
        return pet.toResponse()
    }
}

@Service
class GetAllPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: UUID): List<PetResponse> {
        logger.info("Buscando todos os pets do tutor: $ownerId")
        return petRepository.findByOwnerId(ownerId).map { it.toResponse() }
    }
}

@Service
class UpdatePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdatePetRequest, ownerId: UUID): PetResponse {
        logger.info("Atualizando pet ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para atualizar este pet")
        }

        val updatedPet = pet.update(
            name = request.name,
            breed = request.breed,
            weight = request.weight
        )

        val saved = petRepository.save(updatedPet)
        logger.info("Pet atualizado com sucesso. ID: $id")
        
        return saved.toResponse()
    }
}

@Service
class DeletePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID) {
        logger.info("Excluindo pet ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para excluir este pet")
        }

        petRepository.delete(id)
        logger.info("Pet excluído com sucesso. ID: $id")
    }
}

@Service
class ToggleFavoritePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): PetResponse {
        logger.info("Alternando favorito do pet ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para favoritar este pet")
        }

        val updatedPet = pet.toggleFavorite()
        val saved = petRepository.save(updatedPet)
        
        logger.info("Status de favorito alternado com sucesso. ID: $id")
        return saved.toResponse()
    }
}

@Service
class UpdateHealthStatusUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdateHealthStatusRequest, ownerId: UUID): PetResponse {
        logger.info("Atualizando status de saúde do pet ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para atualizar este pet")
        }

        val updatedPet = pet.updateHealthStatus(request.healthStatus)
        val saved = petRepository.save(updatedPet)
        
        logger.info("Status de saúde atualizado com sucesso. ID: $id")
        return saved.toResponse()
    }
}

@Service
class SearchPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(query: String, ownerId: UUID): List<PetResponse> {
        logger.info("Buscando pets com query: $query")
        return petRepository.searchByNameAndOwnerId(query, ownerId).map { it.toResponse() }
    }
}

@Service
class GetFavoritePetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: UUID): List<PetResponse> {
        logger.info("Buscando pets favoritos do tutor: $ownerId")
        return petRepository.findFavoritesByOwnerId(ownerId).map { it.toResponse() }
    }
}

private fun Pet.toResponse() = PetResponse(
    id = this.id.toString(),
    name = this.name,
    species = this.species,
    breed = this.breed,
    birthDate = this.birthDate.toString(),
    age = this.calculateAge(),
    weight = this.weight,
    ownerId = this.ownerId.toString(),
    isFavorite = this.isFavorite,
    healthStatus = this.healthStatus.name,
    active = this.active,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
