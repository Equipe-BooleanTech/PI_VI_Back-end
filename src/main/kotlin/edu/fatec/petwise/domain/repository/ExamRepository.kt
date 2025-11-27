package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Exam
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

interface ExamRepository {
    fun findById(id: UUID): Optional<Exam>
    fun findByPetId(petId: UUID?): List<Exam>
    fun findByVeterinaryId(veterinaryId: UUID): List<Exam>
    fun findByPetIdAndStatus(petId: UUID, status: String): List<Exam>
    fun findByExamType(examType: String): List<Exam>
    fun findByPetIdAndExamDateBetween(petId: UUID, startDate: LocalDateTime, endDate: LocalDateTime): List<Exam>
    fun findByExamTypeContainingIgnoreCaseOrResultsContainingIgnoreCaseOrNotesContainingIgnoreCase(
        examType: String,
        results: String,
        notes: String
    ): List<Exam>
    fun existsByPetIdAndVeterinaryIdNot(petId: UUID, veterinaryId: UUID): Boolean
    fun existsByPetId(petId: UUID): Boolean
    fun save(exam: Exam): Exam
    fun deleteById(id: UUID)
    fun deleteByPetId(petId: UUID)
}
