package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Exam
import java.time.LocalDateTime
import java.util.UUID

data class ExamResponse(
    val id: UUID?,
    val petId: UUID,
    val veterinaryId: UUID,
    val examType: String,
    val examDate: LocalDateTime,
    val results: String? = null,
    val status: String,
    val notes: String? = null,
    val attachmentUrl: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun fromEntity(exam: Exam): ExamResponse {
            return ExamResponse(
                id = exam.id,
                petId = exam.petId,
                veterinaryId = exam.veterinaryId,
                examType = exam.examType,
                examDate = exam.examDate,
                results = exam.results,
                status = exam.status,
                notes = exam.notes,
                attachmentUrl = exam.attachmentUrl,
                createdAt = exam.createdAt,
                updatedAt = exam.updatedAt
            )
        }
    }
}
