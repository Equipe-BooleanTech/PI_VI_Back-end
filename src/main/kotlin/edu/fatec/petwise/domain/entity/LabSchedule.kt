package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID


@Entity
@Table(name = "lab_schedules")
data class LabSchedule(
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @NotNull
    val pet: Pet,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    @NotNull
    val labTest: LabTest,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    @NotNull
    val veterinarian: User,

    @Column(name = "schedule_date", nullable = false)
    @NotNull
    val scheduleDate: LocalDateTime,

    @Column(name = "appointment_time", nullable = false)
    @NotNull
    val appointmentTime: LocalTime,

    @Column(name = "duration", nullable = false)
    @NotNull
    val duration: Integer, // em minutos

    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    @Size(max = 20)
    val status: String = "SCHEDULED", // SCHEDULED, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW

    @Column(name = "priority", nullable = false, length = 20)
    @NotNull
    @Size(max = 20)
    val priority: String = "ROUTINE", // ROUTINE, URGENT, EMERGENCY

    @Column(name = "notes", length = 1000)
    @Size(max = 1000)
    val notes: String? = null,

    @Column(name = "confirmation_date")
    val confirmationDate: LocalDateTime? = null,

    @Column(name = "completed_at")
    val completedAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    fun prePersist() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}