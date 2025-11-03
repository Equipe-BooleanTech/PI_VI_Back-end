package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.VaccineRequest
import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import edu.fatec.petwise.domain.repository.VaccineTypeRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID
import java.time.LocalDateTime

@Service
class CreateVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val vaccineTypeRepository: VaccineTypeRepository,
    private val petRepository: PetRepository
) {
    fun execute(request: VaccineRequest, authentication: Authentication, petId: UUID): VaccineResponse {
        val userId = UUID.fromString(authentication.principal.toString())

        // Verifica se o tipo de vacina existe e está ativo
        val vaccineType = vaccineTypeRepository.findByIdAndActiveTrue(request.vaccineTypeId)
            ?: throw IllegalArgumentException("Tipo de vacina não encontrado")

        // Verifica se o pet pertence ao usuário autenticado
        val pet = petRepository.findByIdAndOwnerId(petId, userId)
            ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")

        // Verifica se a espécie do pet corresponde à do tipo de vacina
        if (!pet.especie.equals(vaccineType.species, ignoreCase = true)) {
            throw IllegalArgumentException("Espécie do pet não corresponde ao tipo de vacina")
        }

        // Verifica se o número da dose é válido
        val totalVaccinesOfType = vaccineRepository.countByPetIdAndVaccineTypeId(petId, request.vaccineTypeId)
        if (request.doseNumber.toLong() != totalVaccinesOfType + 1) {
            throw IllegalArgumentException("Número da dose inválido. Esperado: ${totalVaccinesOfType + 1}")
        }

        // Cria a vacina
        val vaccine = Vaccine(
            id = UUID.randomUUID(),
            userId = userId,
            petId = petId,
            vaccineTypeId = request.vaccineTypeId,
            veterinarian = request.veterinarian,
            vaccinationDate = request.vaccinationDate,
            batchNumber = request.batchNumber,
            manufacturer = request.manufacturer,
            doseNumber = request.doseNumber,
            totalDoses = request.totalDoses,
            validUntil = request.validUntil,
            siteOfInjection = request.siteOfInjection,
            reactions = request.reactions,
            observations = request.observations,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedVaccine = vaccineRepository.save(vaccine)
        return savedVaccine.toVaccineResponse()
    }
}
