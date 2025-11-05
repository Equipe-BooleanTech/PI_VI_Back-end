package edu.fatec.petwise.application.dto


import edu.fatec.petwise.domain.entity.Sale
import edu.fatec.petwise.domain.enums.PaymentMethod
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

data class SaleDTO(
    val id: UUID? = null,

    @field:NotBlank(message = "Número da venda é obrigatório")
    @field:Size(max = 50, message = "Número da venda deve ter no máximo 50 caracteres")
    val saleNumber: String,

    @field:Size(max = 255, message = "Nome do cliente deve ter no máximo 255 caracteres")
    val customerName: String? = null,

    @field:Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]*$", message = "Formato de telefone inválido")
    val customerPhone: String? = null,

    @field:Size(max = 255, message = "Nome do veterinário deve ter no máximo 255 caracteres")
    val veterinarianName: String? = null,

    @field:NotBlank(message = "Nome do medicamento é obrigatório")
    @field:Size(max = 255, message = "Nome do medicamento deve ter no máximo 255 caracteres")
    val medicationName: String,

    @field:Min(value = 1, message = "Quantidade deve ser maior que zero")
    val quantity: Int,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Preço unitário deve ser maior que zero")
    val unitPrice: BigDecimal,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "Valor total deve ser maior que zero")
    val totalAmount: BigDecimal,

    @field:NotNull(message = "Data da venda é obrigatória")
    val saleDate: LocalDateTime,

    @field:Size(max = 100, message = "Número da receita deve ter no máximo 100 caracteres")
    val prescriptionNumber: String? = null,

    @field:Size(max = 100, message = "Número do lote deve ter no máximo 100 caracteres")
    val batchNumber: String? = null,

    @field:NotNull(message = "Método de pagamento é obrigatório")
    val paymentMethod: PaymentMethod,

    val isPrescriptionRequired: Boolean = false,

    @field:Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    val notes: String? = null,

    val createdAt: LocalDateTime? = null
) {

    companion object {

        fun fromEntity(entity: Sale): SaleDTO {
            return SaleDTO(
                id = entity.id,
                saleNumber = entity.saleNumber,
                customerName = entity.customerName,
                customerPhone = entity.customerPhone,
                veterinarianName = entity.veterinarianName,
                medicationName = entity.medicationName,
                quantity = entity.quantity,
                unitPrice = entity.unitPrice,
                totalAmount = entity.totalAmount,
                saleDate = entity.saleDate,
                prescriptionNumber = entity.prescriptionNumber,
                batchNumber = entity.batchNumber,
                paymentMethod = entity.paymentMethod,
                isPrescriptionRequired = entity.isPrescriptionRequired,
                notes = entity.notes,
                createdAt = entity.createdAt
            )
        }
    }

    fun validateTotalAmount(): Boolean {
        val calculatedTotal = unitPrice.multiply(BigDecimal(quantity))
        return if (totalAmount == calculatedTotal) {
            true
        } else {
            throw IllegalArgumentException(
                "Valor total inválido. Total calculado: $calculatedTotal, mas foi informado: $totalAmount"
            )
        }
    }

    fun toEntity(): Sale {
        return Sale(
            id = this.id,
            saleNumber = this.saleNumber,
            customerName = this.customerName,
            customerPhone = this.customerPhone,
            veterinarianName = this.veterinarianName,
            medicationName = this.medicationName,
            quantity = this.quantity,
            unitPrice = this.unitPrice,
            totalAmount = this.totalAmount,
            saleDate = this.saleDate,
            prescriptionNumber = this.prescriptionNumber,
            batchNumber = this.batchNumber,
            paymentMethod = this.paymentMethod,
            isPrescriptionRequired = this.isPrescriptionRequired,
            notes = this.notes,
            createdAt = this.createdAt
        )
    }
}