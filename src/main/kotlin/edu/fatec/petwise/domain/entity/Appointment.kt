package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.AppointmentStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID



@Entity
@Table(name = "appointments")
data class Appointment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),
    @Column(name = "pet_id", nullable = false)
    val petId: UUID,

    @Column(name = "owner_id", nullable = false)
    val ownerId: UUID,

    @Column(name = "veterinary_id", nullable = false)
    var veterinaryId: UUID,

    @Column(name = "appointment_datetime", nullable = false)
    var appointmentDatetime: LocalDateTime,

    @Column(name = "duration_minutes", nullable = false)
    var durationMinutes: Int = 30,

    @Column(nullable = false, length = 200)
    var motivo: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: AppointmentStatus = AppointmentStatus.AGENDADA,

    @Column(name = "observacoes_cliente", columnDefinition = "TEXT")
    var observacoesCliente: String? = null,

    @Column(name = "observacoes_veterinario", columnDefinition = "TEXT")
    var observacoesVeterinario: String? = null,

    @Column
    var valor: Double? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun podeCancelar(): Boolean {
        return status in listOf(AppointmentStatus.AGENDADA, AppointmentStatus.CONFIRMADA)
    }

    fun podeAtualizar(): Boolean {
        return status in listOf(AppointmentStatus.AGENDADA, AppointmentStatus.CONFIRMADA)
    }
}
