package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.VaccineRequest
import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpdateVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, request: VaccineRequest, authentication: Authentication): VaccineResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem atualizar vacinas")
        }

        val existingVaccine = vaccineRepository.findById(id).orElseThrow { IllegalArgumentException("Vacina não encontrada") }

        
        existingVaccine.vaccineType = request.vaccineType
        existingVaccine.vaccinationDate = request.vaccinationDate
        existingVaccine.nextDoseDate = request.nextDoseDate
        existingVaccine.totalDoses = request.totalDoses
        existingVaccine.manufacturer = request.manufacturer
        existingVaccine.observations = request.observations
        existingVaccine.status = request.status
        existingVaccine.updatedAt = LocalDateTime.now()

        val savedVaccine = vaccineRepository.save(existingVaccine)
        return savedVaccine.toVaccineResponse()
    }
}
