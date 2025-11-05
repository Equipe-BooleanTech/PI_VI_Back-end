package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.entity.LabResult
import edu.fatec.petwise.domain.entity.LabSample
import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "lab_tests")
data class LabTest(
    @Id
    @GeneratedValue
    @Column(name = "id")
    val id: UUID? = null,

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "test_name", nullable = false, length = 100)
    val testName: String,

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "test_code", nullable = false, length = 20)
    val testCode: String,

    @Size(max = 500)
    @Column(name = "description", length = 500)
    val description: String? = null,

    @NotNull
    @Size(min = 5, max = 50)
    @Column(name = "category", nullable = false, length = 50)
    val category: String, // HEMATOLOGY, BIOCHEMISTRY, PARASITOLOGY, VIROLOGY, BACTERIOLOGY, CYTOLOGY, URINALYSIS

    @NotNull
    @Size(min = 4, max = 20)
    @Column(name = "sample_type", nullable = false, length = 20)
    val sampleType: String, // BLOOD, URINE, FECAL, TISSUE, SWAB

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "normal_range_min", precision = 10, scale = 2)
    val normalRangeMin: BigDecimal? = null,

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "normal_range_max", precision = 10, scale = 2)
    val normalRangeMax: BigDecimal? = null,

    @Size(max = 20)
    @Column(name = "unit", length = 20)
    val unit: String? = null,

    @Min(1)
    @Column(name = "test_duration")
    val testDuration: Int? = null, // em horas

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(name = "created_at", updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "labTest", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val labResults: List<LabResult> = emptyList(),

    @OneToMany(mappedBy = "labTest", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val labSamples: List<LabSample> = emptyList()
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