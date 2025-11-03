package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.application.dto.UpdatePetRequest
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UpdatePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, petId: String, request: UpdatePetRequest): PetResponse {
        val ownerId = UUID.fromString(userId)
        val petUuid = UUID.fromString(petId)
        
        val pet = petRepository.findByIdAndOwnerId(petUuid, ownerId)
            ?: throw Exception("Pet não encontrado ou você não tem permissão para atualizá-lo")
        
        // Validar sexo se fornecido
        request.sexo?.let { sexo ->
            if (sexo.uppercase() !in listOf("M", "F")) {
                throw IllegalArgumentException("Sexo deve ser 'M' ou 'F'")
            }
        }
        
        // Atualizar apenas campos fornecidos
        request.nome?.let { pet.nome = it.trim() }
        request.especie?.let { pet.especie = it.trim().lowercase() }
        request.raca?.let { pet.raca = it.trim() }
        request.sexo?.let { pet.sexo = it.uppercase() }
        request.dataNascimento?.let { pet.dataNascimento = it }
        request.peso?.let { pet.peso = it }
        request.cor?.let { pet.cor = it.trim() }
        request.observacoes?.let { pet.observacoes = it.trim() }
        request.fotoUrl?.let { pet.fotoUrl = it.trim() }
        request.ativo?.let { pet.ativo = it }
        
        val updatedPet = petRepository.save(pet)
        
        logger.info("Pet $petId atualizado com sucesso pelo usuário $userId")
        
        return PetResponse.fromEntity(updatedPet)
    }
}
