package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.domain.repository.MedicationRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class DeleteMedicationUseCase(
    private val medicationRepository: MedicationRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        // Buscar medicação
        val medication = medicationRepository.findByIdAndUserIdAndActiveTrue(id, userId)
            ?: throw IllegalArgumentException("Medicação não encontrada ou não pertence ao usuário")
        
        // Soft delete
        val deletedMedication = medication.copy(
            active = false,
            updatedAt = java.time.LocalDateTime.now()
        )
        
        medicationRepository.save(deletedMedication)
    }
}