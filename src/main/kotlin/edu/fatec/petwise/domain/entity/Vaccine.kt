package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.VaccineType
import edu.fatec.petwise.domain.enums.VaccinationStatus
import java.time.LocalDateTime
import java.util.UUID

data class Vaccine(
    val id: UUID? = null,
    val petId: UUID,
    val vaccineName: String,
    val vaccineType: VaccineType,
    val applicationDate: String,
    val nextDoseDate: String?,
    val doseNumber: Int,
    val totalDoses: Int,
    val veterinaryId: UUID,
    val clinicName: String,
    val batchNumber: String,
    val manufacturer: String,
    val observations: String = "",
    val sideEffects: String = "",
    val status: VaccinationStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(vaccineName.isNotBlank()) { "Nome da vacina não pode estar vazio" }
        require(applicationDate.isNotBlank()) { "Data de aplicação não pode estar vazia" }
        require(batchNumber.isNotBlank()) { "Número do lote não pode estar vazio" }
        require(manufacturer.isNotBlank()) { "Fabricante não pode estar vazio" }
        require(clinicName.isNotBlank()) { "Nome da clínica não pode estar vazio" }
        require(doseNumber > 0) { "Número da dose deve ser maior que zero" }
        require(totalDoses > 0) { "Total de doses deve ser maior que zero" }
        require(doseNumber <= totalDoses) { "Número da dose não pode ser maior que o total de doses" }
    }

    fun apply(): Vaccine = this.copy(
        status = VaccinationStatus.APLICADA,
        updatedAt = LocalDateTime.now()
    )

    fun markAsDelayed(): Vaccine = this.copy(
        status = VaccinationStatus.ATRASADA,
        updatedAt = LocalDateTime.now()
    )

    fun cancel(): Vaccine = this.copy(
        status = VaccinationStatus.CANCELADA,
        updatedAt = LocalDateTime.now()
    )

    fun addObservations(observations: String): Vaccine = this.copy(
        observations = observations,
        updatedAt = LocalDateTime.now()
    )

    fun addSideEffects(sideEffects: String): Vaccine = this.copy(
        sideEffects = sideEffects,
        updatedAt = LocalDateTime.now()
    )
}
