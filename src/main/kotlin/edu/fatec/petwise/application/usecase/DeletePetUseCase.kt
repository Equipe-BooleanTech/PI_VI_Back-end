package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.MessageResponse
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeletePetUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, petId: String, forceDelete: Boolean = false): MessageResponse {
        val ownerId = UUID.fromString(userId)
        val petUuid = UUID.fromString(petId)
        
        val pet = petRepository.findByIdAndOwnerId(petUuid, ownerId)
            ?: throw Exception("Pet não encontrado ou você não tem permissão para removê-lo")
        
        // Verificar se existem consultas futuras para este pet
        val futureAppointments = appointmentRepository.findByPetIdOrderByAppointmentDatetimeDesc(petUuid)
            .filter { it.appointmentDatetime.isAfter(java.time.LocalDateTime.now()) }
            .filter { it.status in listOf(
                edu.fatec.petwise.domain.entity.AppointmentStatus.AGENDADA,
                edu.fatec.petwise.domain.entity.AppointmentStatus.CONFIRMADA
            )}
        
        if (futureAppointments.isNotEmpty()) {
            throw IllegalStateException(
                "Não é possível remover o pet pois existem ${futureAppointments.size} consulta(s) futura(s) agendada(s). " +
                "Cancele as consultas primeiro."
            )
        }
        
        if (forceDelete) {
            // Hard delete - remove fisicamente do banco
            petRepository.delete(pet)
            logger.warn("Pet $petId removido permanentemente pelo usuário $userId")
            return MessageResponse("Pet removido permanentemente do sistema")
        } else {
            // Soft delete - marca como inativo
            pet.ativo = false
            petRepository.save(pet)
            logger.info("Pet $petId desativado pelo usuário $userId")
            return MessageResponse("Pet desativado com sucesso")
        }
    }
}
