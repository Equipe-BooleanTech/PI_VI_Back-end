package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ExamRequest
import edu.fatec.petwise.application.dto.ExamResponse
import edu.fatec.petwise.domain.entity.Exam
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.ExamRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class CreateExamUseCase(
    private val examRepository: ExamRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {
    fun execute(request: ExamRequest, authentication: Authentication, petId: UUID): ExamResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        // Only VETERINARY users can create exams
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem criar exames")
        }

        // Verifica se o pet existe
        val pet = petRepository.findById(petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }

        // Cria o exame
        val exam = Exam(
            id = UUID.randomUUID(),
            petId = petId,
            veterinaryId = userId,
            examType = request.examType,
            examDate = request.examDate,
            results = request.results,
            status = request.status,
            notes = request.notes,
            attachmentUrl = request.attachmentUrl,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedExam = examRepository.save(exam)
        return ExamResponse.fromEntity(savedExam)
    }
}
