package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MedicalRecordResponse
import edu.fatec.petwise.domain.repository.MedicalRecordRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDate
import java.util.UUID

@Service
class ListPetMedicalHistoryUseCase(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val petRepository: PetRepository
) {
    fun execute(petId: UUID, authentication: Authentication, startDate: LocalDate?, endDate: LocalDate?): List<MedicalRecordResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        
        // Verificar se o pet pertence ao usuário
        val pet = petRepository.findByIdAndOwnerId(petId, userId)
            ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")
        
        val records = if (startDate != null && endDate != null) {
            medicalRecordRepository.findByPetIdAndRecordDateBetween(
                petId, 
                startDate.atStartOfDay(), 
                endDate.atTime(23, 59, 59)
            )
        } else {
            medicalRecordRepository.findByPetIdAndActiveTrueOrderByRecordDateDesc(petId)
        }
        
        return records.map { it.toMedicalRecordResponse() }
    }
}