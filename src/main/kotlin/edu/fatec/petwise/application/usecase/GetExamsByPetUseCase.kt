package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ExamResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.ExamRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class GetExamsByPetUseCase(
    private val examRepository: ExamRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, petId: UUID): List<ExamResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        
        val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        
        if (user.userType == UserType.OWNER && pet.ownerId != userId) {
            throw IllegalArgumentException("Pet não pertence ao usuário")
        }

        val exams = examRepository.findByPetId(petId)

        return exams.map { it.toExamResponse() }
    }
}