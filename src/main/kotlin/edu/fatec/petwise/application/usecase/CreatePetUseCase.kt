package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreatePetRequest
import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreatePetUseCase(
    private val petRepository: PetRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, request: CreatePetRequest): PetResponse {
        val ownerId = UUID.fromString(userId)
        
        // Validar sexo se fornecido
        request.sexo?.let { sexo ->
            if (sexo.uppercase() !in listOf("M", "F")) {
                throw IllegalArgumentException("Sexo deve ser 'M' ou 'F'")
            }
        }
        
        // Criar entidade Pet
        val pet = Pet(
            ownerId = ownerId,
            nome = request.nome.trim(),
            especie = request.especie.trim().lowercase(),
            raca = request.raca?.trim(),
            sexo = request.sexo?.uppercase(),
            dataNascimento = request.dataNascimento,
            peso = request.peso,
            cor = request.cor?.trim(),
            observacoes = request.observacoes?.trim(),
            fotoUrl = request.fotoUrl?.trim()
        )
        
        val savedPet = petRepository.save(pet)
        
        logger.info("Pet criado com sucesso: ID=${savedPet.id}, Nome=${savedPet.nome}, Dono=$userId")
        
        return PetResponse.fromEntity(savedPet)
    }
}
