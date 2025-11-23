package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.application.dto.ExamResponse
import java.time.LocalDateTime
import java.util.UUID


class Exam(
    var id: UUID? = null,
    val petId: UUID,
    val veterinaryId: UUID,
    var examType: String,
    var examDate: LocalDateTime,
    var results: String?,
    var status: String,
    var notes: String?,
    var attachmentUrl: String?,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {

    fun toExamResponse(): ExamResponse = ExamResponse.fromEntity(this)
}
