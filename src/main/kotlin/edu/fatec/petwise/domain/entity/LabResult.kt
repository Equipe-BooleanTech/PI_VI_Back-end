package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "lab_results")
data class LabResult(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @NotNull
    @Column(name = "pet_id")
    val petId: UUID,

    @NotNull
    @Column(name = "test_id")
    val testId: UUID,

    @NotNull
    @Column(name = "result_date")
    val resultDate: LocalDateTime,

    @NotNull
    @Size(max = 500)
    @Column(name = "result_value")
    val resultValue: String,

    @NotNull
    @Size(max = 50)
    @Column(name = "result_unit")
    val resultUnit: String,

    @NotNull
    @Size(max = 500)
    @Column(name = "reference_value")
    val referenceValue: String,

    @NotNull
    @Size(max = 20)
    @Column(name = "status")
    val status: String,

    @NotNull
    @Column(name = "veterinarian_id")
    val veterinarianId: UUID,

    @Size(max = 1000)
    @Column(name = "notes")
    val notes: String? = null,

    @NotNull
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @NotNull
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "pet_id", insertable = false, updatable = false)
    val pet: Pet,

    @ManyToOne
    @JoinColumn(name = "test_id", insertable = false, updatable = false)
    val labTest: LabTest,

    @ManyToOne
    @JoinColumn(name = "veterinarian_id", insertable = false, updatable = false)
    val veterinarian: User
) {


    @PrePersist
    fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }
    
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}