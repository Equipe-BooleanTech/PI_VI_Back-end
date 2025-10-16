package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.PetFilterRequest
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
            breed = request.breed,
            species = request.species,
            gender = request.gender,
            age = request.age,
            weight = request.weight,
            ownerId = ownerId,
            healthStatus = request.healthStatus,
            healthHistory = request.healthHistory,
            profileImageUrl = request.profileImageUrl
        )

        val saved = petRepository.save(pet)
        logger.info("Pet criado com sucesso. ID: ${saved.id}")
        
        return saved.toResponse(owner.fullName, owner.phone?.value ?: "")
    }
}

@Service
class GetPetByIdUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, ownerId: UUID): PetResponse {
        logger.info("Buscando pet por ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        if (pet.ownerId != ownerId) {
            throw BusinessRuleException("Você não tem permissão para visualizar este pet")
        }
        
        val owner = userRepository.findById(ownerId)!!
        return pet.toResponse(owner.fullName, owner.phone?.value ?: "")
    }
}

@Service
class GetAllPetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: UUID): List<PetResponse> {
        logger.info("Buscando todos os pets do owner: $ownerId")
        val owner = userRepository.findById(ownerId)!!
        return petRepository.findByOwnerId(ownerId).map { 
            it.toResponse(owner.fullName, owner.phone?.value ?: "")
        }
    }
}

@Service
class UpdatePetUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
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
            weight = request.weight,
            age = request.age,
            healthHistory = request.healthHistory,
            profileImageUrl = request.profileImageUrl,
            nextAppointment = request.nextAppointment
        )

        val saved = petRepository.save(updatedPet)
        logger.info("Pet atualizado com sucesso. ID: $id")
        
        val owner = userRepository.findById(ownerId)!!
        return saved.toResponse(owner.fullName, owner.phone?.value ?: "")
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
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
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
        
        val owner = userRepository.findById(ownerId)!!
        logger.info("Status de favorito alternado com sucesso. ID: $id")
        return saved.toResponse(owner.fullName, owner.phone?.value ?: "")
    }
}

@Service
class UpdateHealthStatusUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
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
        
        val owner = userRepository.findById(ownerId)!!
        logger.info("Status de saúde atualizado com sucesso. ID: $id")
        return saved.toResponse(owner.fullName, owner.phone?.value ?: "")
    }
}

@Service
class SearchPetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(query: String, ownerId: UUID): List<PetResponse> {
        logger.info("Buscando pets com query: $query")
        val owner = userRepository.findById(ownerId)!!
        return petRepository.searchByNameAndOwnerId(query, ownerId).map { it.toResponse(owner.fullName, owner.phone?.value ?: "") }
    }
}

@Service
class GetFavoritePetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(ownerId: UUID): List<PetResponse> {
        logger.info("Buscando pets favoritos do owner: $ownerId")
        val owner = userRepository.findById(ownerId)!!
        return petRepository.findFavoritesByOwnerId(ownerId).map { 
            it.toResponse(owner.fullName, owner.phone?.value ?: "")
        }
    }
}

@Service
class FilterPetsUseCase(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(filterOptions: PetFilterRequest, ownerId: UUID): List<PetResponse> {
        logger.info("Filtrando pets do owner: $ownerId")
        val owner = userRepository.findById(ownerId)!!
        
        var pets = if (filterOptions.favoritesOnly) {
            petRepository.findFavoritesByOwnerId(ownerId)
        } else {
            petRepository.findByOwnerId(ownerId)
        }
        
        if (filterOptions.species != null) {
            pets = pets.filter { it.species == filterOptions.species }
        }
        
        if (filterOptions.healthStatus != null) {
            pets = pets.filter { it.healthStatus == filterOptions.healthStatus }
        }
        
        if (filterOptions.searchQuery.isNotEmpty()) {
            pets = pets.filter { 
                it.name.contains(filterOptions.searchQuery, ignoreCase = true) ||
                it.breed.contains(filterOptions.searchQuery, ignoreCase = true)
            }
        }
        
        return pets.map { it.toResponse(owner.fullName, owner.phone?.value ?: "") }
    }
}

private fun Pet.toResponse(ownerName: String, ownerPhone: String) = PetResponse(
    id = this.id.toString(),
    name = this.name,
    breed = this.breed,
    species = this.species.displayName,
    gender = this.gender.displayName,
    age = this.age,
    weight = this.weight,
    healthStatus = this.healthStatus.displayName,
    ownerName = ownerName,
    ownerPhone = ownerPhone,
    healthHistory = this.healthHistory,
    profileImageUrl = this.profileImageUrl,
    isFavorite = this.isFavorite,
    nextAppointment = this.nextAppointment,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
