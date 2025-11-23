package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.MedicationRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class DeleteMedicationUseCase(
    private val medicationRepository: MedicationRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PHARMACY) {
            throw IllegalArgumentException("Apenas farmácias podem deletar medicações")
        }

        if (!medicationRepository.existsById(id)) {
            throw IllegalArgumentException("Medicação não encontrada")
        }

        medicationRepository.deleteById(id)
    }
}
