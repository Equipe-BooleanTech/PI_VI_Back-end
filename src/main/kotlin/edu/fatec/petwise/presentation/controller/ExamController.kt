package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.ExamRequest
import edu.fatec.petwise.application.dto.ExamResponse
import edu.fatec.petwise.application.usecase.CreateExamUseCase
import edu.fatec.petwise.application.usecase.DeleteExamUseCase
import edu.fatec.petwise.application.usecase.ListExamsUseCase
import edu.fatec.petwise.application.usecase.UpdateExamUseCase
import edu.fatec.petwise.application.usecase.SearchExamsUseCase
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.ExamRepository
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = ["*"])
class ExamController(
    private val createExamUseCase: CreateExamUseCase,
    private val listExamsUseCase: ListExamsUseCase,
    private val updateExamUseCase: UpdateExamUseCase,
    private val deleteExamUseCase: DeleteExamUseCase,
    private val searchExamsUseCase: SearchExamsUseCase,
    private val examRepository: ExamRepository,
    private val petRepository: PetRepository,
    private val userRepository: UserRepository
) {

    @GetMapping
    fun listExams(
        authentication: Authentication,
        @RequestParam(required = false) petId: UUID?
    ): ResponseEntity<List<ExamResponse>> {
        val exams = listExamsUseCase.execute(authentication, petId)
        return ResponseEntity.ok(exams)
    }

    @GetMapping("/{id}")
    fun getExamById(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<ExamResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val exam = examRepository.findById(id).orElseThrow { IllegalArgumentException("Exame não encontrado") }

        // Check permissions
        when (user.userType) {
            UserType.VETERINARY -> {
                // Veterinarians can access any exam
            }
            UserType.OWNER -> {
                // Owners can only access exams for their pets
                val pet = petRepository.findById(exam.petId).orElseThrow { IllegalArgumentException("Pet não encontrado") }
                if (pet.ownerId != userId) {
                    throw IllegalArgumentException("Acesso negado")
                }
            }
            else -> throw IllegalArgumentException("Acesso negado")
        }

        return ResponseEntity.ok(ExamResponse.fromEntity(exam))
    }

    @PostMapping
    fun createExam(
        @Valid @RequestBody request: ExamRequest,
        @RequestParam petId: UUID,
        authentication: Authentication
    ): ResponseEntity<ExamResponse> {
        val exam = createExamUseCase.execute(
            request = request,
            authentication = authentication,
            petId = petId
        )
        return ResponseEntity.ok(exam)
    }

    @PutMapping("/{id}")
    fun updateExam(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ExamRequest,
        authentication: Authentication
    ): ResponseEntity<ExamResponse> {
        val exam = updateExamUseCase.execute(id, request, authentication)
        return ResponseEntity.ok(exam)
    }

    @DeleteMapping("/{id}")
    fun deleteExam(
        @PathVariable id: UUID,
        authentication: Authentication
    ): ResponseEntity<Void> {
        deleteExamUseCase.execute(id, authentication)
        return ResponseEntity.noContent().build()
    }
}
