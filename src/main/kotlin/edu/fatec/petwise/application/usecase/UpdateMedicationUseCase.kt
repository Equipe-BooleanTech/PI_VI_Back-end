package edu.fatec.petwise.application.usecase

import com.petwise.dto.MedicationResponse
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpdateMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val petRepository: PetRepository
) {
    fun execute(id: UUID, authentication: Authentication, administered: Boolean?, administrationNotes: String?): MedicationResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        
        // Buscar medicação
        val medication = medicationRepository.findByIdAndUserIdAndActiveTrue(id, userId)
            ?: throw IllegalArgumentException("Medicação não encontrada ou não pertence ao usuário")
        
        // Verificar se o pet pertence ao usuário
        val pet = petRepository.findByIdAndOwnerId(medication.petId, userId)
            ?: throw IllegalArgumentException("Pet não encontrado ou não pertence ao usuário")
        
        // Atualizar campos
        val updatedMedication = medication.copy(
            administered = administered ?: medication.administered,
            administeredAt = if (administered == true && medication.administeredAt == null) {
                LocalDateTime.now()
            } else medication.administeredAt,
            administrationNotes = administrationNotes ?: medication.administrationNotes,
            updatedAt = LocalDateTime.now()
        )
        
        val savedMedication = medicationRepository.save(updatedMedication)
        return savedMedication.toMedicationResponse()
    }
}