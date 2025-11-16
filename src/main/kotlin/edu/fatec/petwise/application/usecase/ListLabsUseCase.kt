package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.LabResponse
import edu.fatec.petwise.domain.repository.LabRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication

@Service
class ListLabsUseCase(
    private val labRepository: LabRepository
) {
    fun execute(authentication: Authentication): List<LabResponse> {
        val labs = labRepository.findAll()
        return labs.map { LabResponse.fromEntity(it) }
    }
}
