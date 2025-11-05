package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.domain.entity.Prescription
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class ListPrescriptionsUseCase(
    private val prescriptionRepository: PrescriptionRepository
) {
    fun execute(authentication: Authentication, petId: UUID?, status: String?): List<PrescriptionResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        
        val prescriptions = when {
            petId != null && status != null -> {
                // Filtro por pet e status
                val prescriptionStatus = try {
                    Prescription.PrescriptionStatus.valueOf(status.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("Status inválido: $status")
                }
                prescriptionRepository.findByPetIdAndStatusAndActiveTrue(petId, prescriptionStatus)
            }
            petId != null -> {
                // Filtro apenas por pet
                prescriptionRepository.findByPetIdAndActiveTrueOrderByPrescriptionDateDesc(petId)
            }
            status != null -> {
                // Filtro apenas por status
                val prescriptionStatus = try {
                    Prescription.PrescriptionStatus.valueOf(status.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException("Status inválido: $status")
                }
                prescriptionRepository.findByUserIdAndStatus(userId, prescriptionStatus)
            }
            else -> {
                // Sem filtros - todas as prescrições do usuário
                prescriptionRepository.findByUserIdAndActiveTrue(userId)
            }
        }
        
        return prescriptions.map { it.toPrescriptionResponse() }
    }
}