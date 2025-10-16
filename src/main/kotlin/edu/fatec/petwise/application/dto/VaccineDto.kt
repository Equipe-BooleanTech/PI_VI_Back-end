package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.enums.VaccineType
import edu.fatec.petwise.domain.enums.VaccinationStatus
import jakarta.validation.constraints.*

data class CreateVaccineRequest(
    @field:NotBlank(message = "{vaccine.petId.required}")
    val petId: String,
    
    @field:NotBlank(message = "{vaccine.vaccineName.required}")
    @field:Size(min = 2, max = 100, message = "{vaccine.vaccineName.size}")
    val vaccineName: String,
    
    @field:NotNull(message = "{vaccine.vaccineType.required}")
    val vaccineType: VaccineType,
    
    @field:NotBlank(message = "{vaccine.applicationDate.required}")
    val applicationDate: String,
    
    val nextDoseDate: String? = null,
    
    @field:Min(value = 1, message = "{vaccine.doseNumber.min}")
    val doseNumber: Int,
    
    @field:Min(value = 1, message = "{vaccine.totalDoses.min}")
    val totalDoses: Int,
    
    @field:NotBlank(message = "{vaccine.veterinaryId.required}")
    val veterinaryId: String,
    
    @field:NotBlank(message = "{vaccine.clinicName.required}")
    @field:Size(min = 2, max = 100, message = "{vaccine.clinicName.size}")
    val clinicName: String,
    
    @field:NotBlank(message = "{vaccine.batchNumber.required}")
    @field:Size(min = 1, max = 50, message = "{vaccine.batchNumber.size}")
    val batchNumber: String,
    
    @field:NotBlank(message = "{vaccine.manufacturer.required}")
    @field:Size(min = 2, max = 100, message = "{vaccine.manufacturer.size}")
    val manufacturer: String,
    
    val observations: String = "",
    val sideEffects: String = "",
    
    @field:NotNull(message = "{vaccine.status.required}")
    val status: VaccinationStatus = VaccinationStatus.AGENDADA
)

data class UpdateVaccineRequest(
    val nextDoseDate: String? = null,
    val observations: String? = null,
    val sideEffects: String? = null,
    val status: VaccinationStatus? = null
)

data class VaccineFilterRequest(
    val petId: String? = null,
    val vaccineType: VaccineType? = null,
    val status: VaccinationStatus? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val searchQuery: String = ""
)

data class VaccineResponse(
    val id: String,
    val petId: String,
    val petName: String,
    val vaccineName: String,
    val vaccineType: String,
    val applicationDate: String,
    val nextDoseDate: String?,
    val doseNumber: Int,
    val totalDoses: Int,
    val veterinarianName: String,
    val veterinarianCrmv: String,
    val clinicName: String,
    val batchNumber: String,
    val manufacturer: String,
    val observations: String,
    val sideEffects: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)
