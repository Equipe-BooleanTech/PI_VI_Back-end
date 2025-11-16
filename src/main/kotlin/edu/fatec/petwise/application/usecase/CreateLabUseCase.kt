package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.LabRequest
import edu.fatec.petwise.application.dto.LabResponse
import edu.fatec.petwise.domain.entity.Lab
import edu.fatec.petwise.domain.repository.LabRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime

@Service
class CreateLabUseCase(
    private val labRepository: LabRepository
) {
    fun execute(request: LabRequest, authentication: Authentication): LabResponse {
        val lab = Lab(
            id = null,
            name = request.name,
            contactInfo = request.contactInfo,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedLab = labRepository.save(lab)
        return LabResponse.fromEntity(savedLab)
    }
}
