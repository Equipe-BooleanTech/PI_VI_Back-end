package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.VaccineType
import edu.fatec.petwise.domain.enums.VaccinationStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "vaccines")
class VaccineEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, name = "pet_id")
    var petId: UUID,

    @Column(nullable = false, name = "veterinarian_id")
    var veterinarianId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "vaccine_type")
    var vaccineType: VaccineType,

    @Column(nullable = false, name = "vaccination_date")
    var vaccinationDate: LocalDateTime,

    @Column(name = "next_dose_date")
    var nextDoseDate: LocalDateTime? = null,

    @Column(nullable = false, name = "total_doses")
    var totalDoses: Int,

    @Column(name = "manufacturer")
    var manufacturer: String? = null,

    @Column(columnDefinition = "TEXT")
    var observations: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: VaccinationStatus,

    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
