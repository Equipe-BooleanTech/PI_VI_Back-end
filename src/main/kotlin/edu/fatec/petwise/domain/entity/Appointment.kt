package edu.fatec.petwise.domain.entity

import java.time.LocalDateTime
import java.util.UUID

data class Appointment(
    val id: UUID? = null,
    val petId: UUID,
    val veterinaryId: UUID,
    val ownerId: UUID,
    val scheduledDate: LocalDateTime,
    val reason: String,
    val notes: String? = null,
    val diagnosis: String? = null,
    val treatment: String? = null,
    val status: AppointmentStatus = AppointmentStatus.SCHEDULED,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(reason.isNotBlank()) { "Motivo da consulta não pode estar vazio" }
        require(scheduledDate.isAfter(LocalDateTime.now())) { "Data da consulta deve ser futura" }
    }

    fun confirm(): Appointment = this.copy(
        status = AppointmentStatus.CONFIRMED,
        updatedAt = LocalDateTime.now()
    )

    fun start(): Appointment = this.copy(
        status = AppointmentStatus.IN_PROGRESS,
        updatedAt = LocalDateTime.now()
    )

    fun complete(diagnosis: String?, treatment: String?): Appointment {
        require(!diagnosis.isNullOrBlank()) { "Diagnóstico é obrigatório para finalizar consulta" }
        return this.copy(
            status = AppointmentStatus.COMPLETED,
            diagnosis = diagnosis,
            treatment = treatment,
            updatedAt = LocalDateTime.now()
        )
    }

    fun cancel(): Appointment = this.copy(
        status = AppointmentStatus.CANCELLED,
        updatedAt = LocalDateTime.now()
    )

    fun addNotes(notes: String): Appointment = this.copy(
        notes = notes,
        updatedAt = LocalDateTime.now()
    )

    fun canBeModified(): Boolean = status in listOf(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED)
    fun canBeCancelled(): Boolean = status !in listOf(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED)
}
