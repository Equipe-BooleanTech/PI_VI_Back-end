package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.LabRequest
import edu.fatec.petwise.application.dto.LabResponse
import edu.fatec.petwise.domain.repository.LabRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpdateLabUseCase(
    private val labRepository: LabRepository
) {
    fun execute(id: UUID, request: LabRequest, authentication: Authentication): LabResponse {
        val existingLab = labRepository.findById(id).orElseThrow { IllegalArgumentException("Laboratório não encontrado") }

        existingLab.name = request.name
        existingLab.contactInfo = request.contactInfo
        existingLab.updatedAt = LocalDateTime.now()

        val savedLab = labRepository.save(existingLab)
        return LabResponse.fromEntity(savedLab)
    }
}
