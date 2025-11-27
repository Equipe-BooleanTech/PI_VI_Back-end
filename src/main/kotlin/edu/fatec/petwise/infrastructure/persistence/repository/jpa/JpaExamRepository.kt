package edu.fatec.petwise.infrastructure.persistence.repository.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.ExamEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface JpaExamRepository : JpaRepository<ExamEntity, UUID> {
    fun findByPetId(petId: UUID?): List<ExamEntity>
    fun findByVeterinaryId(veterinaryId: UUID): List<ExamEntity>
    fun findByPetIdAndStatus(petId: UUID, status: String): List<ExamEntity>
    fun findByExamType(examType: String): List<ExamEntity>
    fun findByPetIdAndExamDateBetween(petId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<ExamEntity>
    fun findByExamTypeContainingIgnoreCaseOrResultsContainingIgnoreCaseOrNotesContainingIgnoreCase(
        examType: String,
        results: String,
        notes: String
    ): List<ExamEntity>
    fun existsByPetIdAndVeterinaryIdNot(petId: UUID, veterinaryId: UUID): Boolean
    fun existsByPetId(petId: UUID): Boolean
    fun deleteByPetId(petId: UUID)
}
