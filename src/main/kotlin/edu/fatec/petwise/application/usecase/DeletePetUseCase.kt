package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.repository.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeletePetUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository,
    private val vaccineRepository: VaccineRepository,
    private val prescriptionRepository: PrescriptionRepository,
    private val medicationRepository: MedicationRepository,
    private val examRepository: ExamRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, petId: UUID): MessageResponse {
        val pet = petRepository.findById(petId).orElseThrow { Exception("Pet não encontrado") }

        if (pet.ownerId != userId) {
            throw Exception("Você não tem permissão para remover este pet")
        }

        // Delete related data
        appointmentRepository.deleteByPetId(petId)
        vaccineRepository.deleteByPetId(petId)
        examRepository.deleteByPetId(petId)

        // Get prescriptions for the pet and delete medications for each prescription
        val prescriptions = prescriptionRepository.findByPetId(petId)
        prescriptions.forEach { prescription ->
            medicationRepository.deleteByPrescriptionId(prescription.id!!)
        }
        prescriptionRepository.deleteByPetId(petId)

        // Finally delete the pet
        petRepository.deleteById(petId)
        logger.info("Pet $petId removido pelo usuário $userId")
        return MessageResponse("Pet removido com sucesso")
    }
}
