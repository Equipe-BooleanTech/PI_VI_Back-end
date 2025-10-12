package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.TutorRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreatePetUseCase(
    private val petRepository: PetRepository,
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: CreatePetRequest): PetResponse {
        logger.info("Criando novo pet: ${request.name}")

        val tutorId = UUID.fromString(request.tutorId)
        if (!tutorRepository.existsById(tutorId)) {
            logger.warn("Tentativa de criar pet para tutor inexistente: $tutorId")
            throw EntityNotFoundException("Tutor", tutorId)
        }

        val pet = Pet(
            name = request.name,
            species = request.species,
            breed = request.breed,
            birthDate = request.birthDate,
            weight = request.weight,
            tutorId = tutorId
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

    fun execute(id: UUID): PetResponse {
        logger.info("Buscando pet por ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)
        
        return pet.toResponse()
    }
}

@Service
class GetAllPetsUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(): List<PetResponse> {
        logger.info("Buscando todos os pets")
        return petRepository.findAll().map { it.toResponse() }
    }
}

@Service
class GetPetsByTutorUseCase(
    private val petRepository: PetRepository,
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(tutorId: UUID): List<PetResponse> {
        logger.info("Buscando pets do tutor: $tutorId")
        
        if (!tutorRepository.existsById(tutorId)) {
            throw EntityNotFoundException("Tutor", tutorId)
        }
        
        return petRepository.findByTutorId(tutorId).map { it.toResponse() }
    }
}

@Service
class UpdatePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdatePetRequest): PetResponse {
        logger.info("Atualizando pet ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)

        val updatedPet = pet.update(
            name = request.name,
            breed = request.breed,
            weight = request.weight
        )

        val saved = petRepository.update(updatedPet)
        logger.info("Pet atualizado com sucesso. ID: $id")
        
        return saved.toResponse()
    }
}

@Service
class DeactivatePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID): PetResponse {
        logger.info("Desativando pet ID: $id")
        
        val pet = petRepository.findById(id)
            ?: throw EntityNotFoundException("Pet", id)

        val deactivatedPet = pet.deactivate()
        val saved = petRepository.update(deactivatedPet)
        
        logger.info("Pet desativado com sucesso. ID: $id")
        return saved.toResponse()
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
    tutorId = this.tutorId.toString(),
    active = this.active,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
