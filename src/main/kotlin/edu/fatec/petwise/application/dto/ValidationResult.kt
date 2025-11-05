package edu.fatec.petwise.application.dto

import java.time.LocalDateTime
import java.util.UUID


data class ValidationResult(
    val supplierId: UUID,
    val isValid: Boolean,
    val overallStatus: String,
    val message: String,
    val validations: List<ValidationItem>,
    val validatedAt: LocalDateTime
) {

    data class ValidationItem(
        val field: String,
        val isValid: Boolean,
        val message: String
    )
}
