package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class InventoryItemDTO(
    
    val id: UUID? = null,
    
    @field:NotBlank(message = "Nome do medicamento é obrigatório")
    @field:Size(max = 255, message = "Nome do medicamento não pode exceder 255 caracteres")
    val medicationName: String,
    
    @field:Size(max = 255, message = "Nome genérico não pode exceder 255 caracteres")
    val genericName: String? = null,
    
    @field:Size(max = 2000, message = "Descrição não pode exceder 2000 caracteres")
    val description: String? = null,
    
    @field:Min(value = 0, message = "Estoque atual deve ser não negativo")
    val currentStock: Int = 0,
    
    @field:Min(value = 0, message = "Estoque mínimo deve ser não negativo")
    val minimumStock: Int = 0,
    
    @field:Min(value = 0, message = "Estoque máximo deve ser não negativo")
    val maximumStock: Int = 0,
    
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Preço unitário deve ser positivo")
    val unitPrice: BigDecimal? = null,
    
    @field:Positive(message = "ID do fornecedor deve ser positivo")
    val supplierId: UUID? = null,
    
    @field:Size(max = 100, message = "Número do lote não pode exceder 100 caracteres")
    val batchNumber: String? = null,
    
    @field:Future(message = "Data de validade deve ser futura")
    val expirationDate: LocalDate? = null,
    
    @field:Size(max = 255, message = "Localização não pode exceder 255 caracteres")
    val location: String? = null,
    
    val isActive: Boolean = true,
    
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    
    // Campos de informação adicional
    val supplierName: String? = null,
    val daysUntilExpiration: Int? = null,
    val isLowStock: Boolean = false,
    val stockStatus: String? = null
)
