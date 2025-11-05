package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "lab_samples")
data class LabSample(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    val id: UUID = UUID.randomUUID(),

    @NotNull
    @Size(max = 50)
    @Column(name = "sample_code", nullable = false, unique = true)
    val sampleCode: String,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    val pet: Pet,

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    val test: LabTest,

    @NotNull
    @Size(max = 20)
    @Column(name = "sample_type", nullable = false)
    val sampleType: String, // BLOOD, URINE, FECAL, TISSUE, SWAB

    @NotNull
    @Column(name = "collection_date", nullable = false)
    val collectionDate: LocalDateTime,

    @Size(max = 100)
    @Column(name = "collection_location", nullable = true)
    val collectionLocation: String? = null,

    @NotNull
    @Size(max = 20)
    @Column(name = "status", nullable = false)
    val status: String, // COLLECTED, PROCESSING, ANALYZING, COMPLETED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = true)
    val technician: User?,

    @Size(max = 255)
    @Column(name = "rejection_reason", nullable = true)
    val rejectionReason: String? = null,

    @Size(max = 255)
    @Column(name = "storage_conditions", nullable = true)
    val storageConditions: String? = null,

    @Column(name = "expiration_date", nullable = true)
    val expirationDate: LocalDateTime? = null,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
    
    companion object {
        // Constantes para os tipos de amostra
        const val SAMPLE_TYPE_BLOOD = "BLOOD"
        const val SAMPLE_TYPE_URINE = "URINE"
        const val SAMPLE_TYPE_FECAL = "FECAL"
        const val SAMPLE_TYPE_TISSUE = "TISSUE"
        const val SAMPLE_TYPE_SWAB = "SWAB"
        
        // Constantes para os status
        const val STATUS_COLLECTED = "COLLECTED"
        const val STATUS_PROCESSING = "PROCESSING"
        const val STATUS_ANALYZING = "ANALYZING"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_REJECTED = "REJECTED"
    }
}