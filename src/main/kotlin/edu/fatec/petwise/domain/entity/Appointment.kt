package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.ConsultaStatus
import edu.fatec.petwise.domain.enums.ConsultaType
import java.time.LocalDateTime
import java.util.UUID


class Appointment(
    var id: UUID? = null,
    val petId: UUID,
    val ownerId: UUID,
    var petName: String,
    var veterinarianName: String,
    var consultaType: ConsultaType,
    var consultaDate: LocalDateTime,
    var consultaTime: String,
    var status: ConsultaStatus = ConsultaStatus.SCHEDULED,
    var symptoms: String = "",
    var diagnosis: String = "",
    var treatment: String = "",
    var prescriptions: String = "",
    var notes: String = "",
    var nextAppointment: LocalDateTime? = null,
    var price: Double = 0.0,
    var isPaid: Boolean = false,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {

    fun canCancel(): Boolean {
        return status in listOf(ConsultaStatus.SCHEDULED, ConsultaStatus.IN_PROGRESS)
    }

    fun canUpdate(): Boolean {
        return status in listOf(ConsultaStatus.SCHEDULED, ConsultaStatus.IN_PROGRESS)
    }
}

data class ConsultaFilterOptions(
    val consultaType: ConsultaType? = null,
    val status: ConsultaStatus? = null,
    val petId: UUID? = null,
    val dateRange: DateRange? = null,
    val searchQuery: String = ""
)

data class DateRange(
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
