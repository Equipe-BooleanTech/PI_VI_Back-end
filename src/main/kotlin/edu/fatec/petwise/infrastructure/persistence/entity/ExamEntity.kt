package edu.fatec.petwise.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "exams")
class ExamEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "pet_id", nullable = false)
    var petId: UUID,

    @Column(name = "veterinary_id", nullable = false)
    var veterinaryId: UUID,

    @Column(name = "exam_type", nullable = false)
    var examType: String,

    @Column(name = "exam_date", nullable = false)
    var examDate: LocalDateTime,

    @Column(name = "results", columnDefinition = "TEXT")
    var results: String? = null,

    @Column(name = "status", nullable = false)
    var status: String,

    @Column(name = "notes", columnDefinition = "TEXT")
    var notes: String? = null,

    @Column(name = "attachment_url")
    var attachmentUrl: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
