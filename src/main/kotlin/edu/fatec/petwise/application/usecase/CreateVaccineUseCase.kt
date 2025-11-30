package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.VaccineRequest
import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreateVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(request: VaccineRequest, authentication: Authentication, petId: UUID): VaccineResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem criar vacinas")
        }

        
        val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        
        val vaccine = Vaccine(
            id = null,
            petId = petId,
            veterinarianId = userId,
            vaccineType = request.vaccineType,
            vaccinationDate = request.vaccinationDate,
            nextDoseDate = request.nextDoseDate,
            totalDoses = request.totalDoses,
            manufacturer = request.manufacturer,
            observations = request.observations,
            status = request.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedVaccine = vaccineRepository.save(vaccine)
        return savedVaccine.toVaccineResponse()
    }
}
