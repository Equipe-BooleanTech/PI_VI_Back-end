package edu.fatec.petwise.application.dto

import java.util.*

data class VaccineTypeResponse(
    val id: UUID,
    val species: String,
    val vaccineName: String,
    val description: String? = null,
    val manufacturer: String? = null,
    val dosesRequired: Int,
    val boosterIntervalMonths: Int? = null,
    val ageRestrictionMonths: Int? = null,
    val active: Boolean
)