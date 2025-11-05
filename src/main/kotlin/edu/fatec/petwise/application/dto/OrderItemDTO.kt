package edu.fatec.petwise.application.dto


import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID


data class OrderItemDTO(
    
    val id: UUID? = null,
    
    @field:NotNull(message = "ID do pedido de farmácia é obrigatório")
    val pharmacyOrderId: UUID,
    
    @field:NotBlank(message = "Nome do medicamento é obrigatório")
    @field:Size(max = 255, message = "Nome do medicamento deve ter no máximo 255 caracteres")
    val medicationName: String,
    
    @field:Size(max = 255, message = "Nome genérico deve ter no máximo 255 caracteres")
    val genericName: String? = null,
    
    @field:Min(value = 1, message = "Quantidade deve ser no mínimo 1")
    val quantity: Int,
    
    @field:DecimalMin(value = "0.0", message = "Preço unitário deve ser no mínimo 0.0")
    val unitPrice: BigDecimal,
    
    @field:DecimalMin(value = "0.0", message = "Preço total deve ser no mínimo 0.0")
    val totalPrice: BigDecimal,
    
    @field:Size(max = 100, message = "Número do lote deve ter no máximo 100 caracteres")
    val batchNumber: String? = null,
    
    @field:Future(message = "Data de validade deve ser futura")
    val expirationDate: LocalDate? = null,
    
    @field:Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    val notes: String? = null,
    
    val createdAt: LocalDateTime? = null,
    
    val updatedAt: LocalDateTime? = null
) {
    
    init {
        val calculatedTotalPrice = unitPrice.multiply(BigDecimal(quantity))
        if (totalPrice != calculatedTotalPrice) {
            throw IllegalArgumentException(
                "Preço total inconsistente. Esperado: $calculatedTotalPrice, informado: $totalPrice"
            )
        }
    }
    
    companion object {
        fun fromEntity(entity: OrderItem): OrderItemDTO {
            return OrderItemDTO(
                id = entity.id,
                pharmacyOrderId = entity.pharmacyOrderId,
                medicationName = entity.medicationName,
                genericName = entity.genericName,
                quantity = entity.quantity,
                unitPrice = entity.unitPrice,
                totalPrice = entity.totalPrice,
                batchNumber = entity.batchNumber,
                expirationDate = entity.expirationDate,
                notes = entity.notes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    fun toEntity(): OrderItem {
        return OrderItem(
            id = this.id,
            pharmacyOrderId = this.pharmacyOrderId,
            medicationName = this.medicationName,
            genericName = this.genericName,
            quantity = this.quantity,
            unitPrice = this.unitPrice,
            totalPrice = this.totalPrice,
            batchNumber = this.batchNumber,
            expirationDate = this.expirationDate,
            notes = this.notes,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }
}
