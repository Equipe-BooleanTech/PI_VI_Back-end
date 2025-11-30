package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.ExamRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class DeleteExamUseCase(
    private val examRepository: ExamRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem excluir exames")
        }

        val exam = examRepository.findById(id).orElseThrow { IllegalArgumentException("Exame não encontrado") }

        examRepository.deleteById(id)
    }
}
