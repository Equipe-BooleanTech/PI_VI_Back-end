package edu.fatec.petwise.infrastructure.persistence.repository.adapter

import edu.fatec.petwise.domain.entity.Exam
import edu.fatec.petwise.domain.repository.ExamRepository
import edu.fatec.petwise.infrastructure.persistence.entity.ExamEntity
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaExamRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Repository
class ExamRepositoryAdapter(
    private val repository: JpaExamRepository
): ExamRepository {
    override fun findById(id: UUID): Optional<Exam> = repository.findById(id).map { it.toDomain() }
    override fun findByPetId(petId: UUID?): List<Exam> = repository.findByPetId(petId).map { it.toDomain() }
    override fun findByVeterinaryId(veterinaryId: UUID): List<Exam> = repository.findByVeterinaryId(veterinaryId).map { it.toDomain() }
    override fun findByPetIdAndStatus(petId: UUID, status: String): List<Exam> = repository.findByPetIdAndStatus(petId, status).map { it.toDomain() }
    override fun findByExamType(examType: String): List<Exam> = repository.findByExamType(examType).map { it.toDomain() }
    override fun findByPetIdAndExamDateBetween(petId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<Exam> = repository.findByPetIdAndExamDateBetween(petId, startDate, endDate).map { it.toDomain() }
    override fun findByExamTypeContainingIgnoreCaseOrResultsContainingIgnoreCaseOrNotesContainingIgnoreCase(examType: String, results: String, notes: String): List<Exam> = repository.findByExamTypeContainingIgnoreCaseOrResultsContainingIgnoreCaseOrNotesContainingIgnoreCase(examType, results, notes).map { it.toDomain() }
    override fun save(exam: Exam): Exam {
        val entity = exam.toEntity()
        val saved = repository.save(entity)
        return saved.toDomain()
    }

    override fun deleteById(id: UUID) = repository.deleteById(id)
    override fun deleteByPetId(petId: UUID) = repository.deleteByPetId(petId)

    private fun ExamEntity.toDomain(): Exam = Exam(
        id = this.id,
        petId = this.petId,
        veterinaryId = this.veterinaryId,
        examType = this.examType,
        examDate = this.examDate,
        results = this.results,
        status = this.status,
        notes = this.notes,
        attachmentUrl = this.attachmentUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun Exam.toEntity(): ExamEntity = ExamEntity(
        petId = this.petId,
        veterinaryId = this.veterinaryId,
        examType = this.examType,
        examDate = this.examDate,
        results = this.results,
        status = this.status,
        notes = this.notes,
        attachmentUrl = this.attachmentUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    ).apply { id = this@toEntity.id }
}
