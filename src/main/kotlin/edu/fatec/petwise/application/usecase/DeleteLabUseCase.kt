package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.repository.LabRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class DeleteLabUseCase(
    private val labRepository: LabRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val lab = labRepository.findById(id).orElseThrow { IllegalArgumentException("Laboratório não encontrado") }

        labRepository.deleteById(id)
    }
}
