package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.ConsultaType
import edu.fatec.petwise.domain.enums.ConsultaStatus
import java.time.LocalDateTime
import java.util.UUID

data class Appointment(
    val id: UUID? = null,
    val petId: UUID,
    val ownerId: UUID,
    val consultaType: ConsultaType,
    val consultaDate: String,
    val consultaTime: String,
    val status: ConsultaStatus = ConsultaStatus.SCHEDULED,
    val symptoms: String = "",
    val diagnosis: String = "",
    val treatment: String = "",
    val prescriptions: String = "",
    val notes: String = "",
    val nextAppointment: String? = null,
    val price: Float = 0f,
    val isPaid: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(consultaDate.isNotBlank()) { "Data da consulta não pode estar vazia" }
        require(consultaTime.isNotBlank()) { "Horário da consulta não pode estar vazio" }
        require(price >= 0) { "Preço não pode ser negativo" }
    }

    fun start(): Appointment = this.copy(
        status = ConsultaStatus.IN_PROGRESS,
        updatedAt = LocalDateTime.now()
    )

    fun complete(diagnosis: String, treatment: String, prescriptions: String = ""): Appointment = this.copy(
        status = ConsultaStatus.COMPLETED,
        diagnosis = diagnosis,
        treatment = treatment,
        prescriptions = prescriptions,
        updatedAt = LocalDateTime.now()
    )

    fun cancel(): Appointment = this.copy(
        status = ConsultaStatus.CANCELLED,
        updatedAt = LocalDateTime.now()
    )

    fun reschedule(newDate: String, newTime: String): Appointment = this.copy(
        status = ConsultaStatus.RESCHEDULED,
        consultaDate = newDate,
        consultaTime = newTime,
        updatedAt = LocalDateTime.now()
    )

    fun markAsPaid(): Appointment = this.copy(
        isPaid = true,
        updatedAt = LocalDateTime.now()
    )

    fun canBeModified(): Boolean = status in listOf(ConsultaStatus.SCHEDULED, ConsultaStatus.RESCHEDULED)
    fun canBeCancelled(): Boolean = status !in listOf(ConsultaStatus.COMPLETED, ConsultaStatus.CANCELLED)
}
