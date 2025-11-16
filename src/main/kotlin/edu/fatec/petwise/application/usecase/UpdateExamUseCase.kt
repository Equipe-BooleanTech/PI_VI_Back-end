package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ExamRequest
import edu.fatec.petwise.application.dto.ExamResponse
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.ExamRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDateTime
import java.util.UUID

@Service
class UpdateExamUseCase(
    private val examRepository: ExamRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, request: ExamRequest, authentication: Authentication): ExamResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        // Only VETERINARY users can update exams
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem atualizar exames")
        }

        val existingExam = examRepository.findById(id).orElseThrow { IllegalArgumentException("Exame não encontrado") }

        // Update the exam
        existingExam.examType = request.examType
        existingExam.examDate = request.examDate
        existingExam.results = request.results
        existingExam.status = request.status
        existingExam.notes = request.notes
        existingExam.attachmentUrl = request.attachmentUrl
        existingExam.updatedAt = LocalDateTime.now()

        val savedExam = examRepository.save(existingExam)
        return ExamResponse.fromEntity(savedExam)
    }
}
