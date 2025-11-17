package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ExamResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.ExamRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class SearchExamsUseCase(
    private val examRepository: ExamRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, query: String): List<ExamResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val exams = when (user.userType) {
            UserType.VETERINARY -> {
                // Veterinarians can search all exams
                examRepository.findByExamTypeContainingIgnoreCaseOrResultsContainingIgnoreCaseOrNotesContainingIgnoreCase(
                    query, query, query
                )
            }
            UserType.OWNER -> {
                // Owners can only search exams for their pets
                val ownerPets = petRepository.findByOwnerId(userId)
                val petIds = ownerPets.map { it.id }
                val allExams = petIds.flatMap { petId ->
                    examRepository.findByPetId(petId)
                }
                allExams.filter { exam ->
                    exam.examType.contains(query, ignoreCase = true) ||
                    (exam.results?.contains(query, ignoreCase = true) ?: false) ||
                    (exam.notes?.contains(query, ignoreCase = true) ?: false)
                }
            }
            else -> emptyList()
        }

        return exams.map { ExamResponse.fromEntity(it) }
    }
}
