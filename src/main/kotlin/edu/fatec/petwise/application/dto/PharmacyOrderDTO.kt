package edu.fatec.petwise.application.dto


import edu.fatec.petwise.domain.entity.PharmacyOrder
import edu.fatec.petwise.domain.enums.PharmacyOrderStatus
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class PharmacyOrderDTO(

    val id: UUID? = null,

    @field:NotBlank(message = "O número do pedido é obrigatório")
    @field:Size(max = 50, message = "O número do pedido deve ter no máximo 50 caracteres")
    val orderNumber: String,
    @field:NotNull(message = "O ID do fornecedor é obrigatório")
    val supplierId: UUID,

    @field:NotNull(message = "A data do pedido é obrigatória")
    val orderDate: LocalDateTime,

    val expectedDeliveryDate: LocalDate? = null,

    val actualDeliveryDate: LocalDate? = null,

    @field:NotNull(message = "O status do pedido é obrigatório")
    val status: PharmacyOrderStatus,

    @field:DecimalMin(value = "0.0", message = "O valor total deve ser maior ou igual a zero")
    val totalAmount: BigDecimal,

    @field:Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
    val notes: String? = null,

    val createdAt: LocalDateTime? = null,

    val updatedAt: LocalDateTime? = null,

    val orderItems: List<OrderItemDTO>? = null
) {


    data class OrderItemDTO(

        val id: UUID? = null,

        @field:NotNull(message = "O ID do medicamento é obrigatório")
        val medicationId: UUID,

        @field:NotBlank(message = "O nome do medicamento é obrigatório")
        @field:Size(max = 255, message = "O nome do medicamento deve ter no máximo 255 caracteres")
        val medicationName: String,

        val genericName: String? = null,

        @field:NotNull(message = "A quantidade é obrigatória")
        val quantity: Int,

        @field:NotNull(message = "O preço unitário é obrigatório")
        @field:DecimalMin(value = "0.0", message = "O preço unitário deve ser maior que zero")
        val unitPrice: BigDecimal,

        @field:NotNull(message = "O preço total é obrigatório")
        @field:DecimalMin(value = "0.0", message = "O preço total deve ser maior que zero")
        val totalPrice: BigDecimal,

        val batchNumber: String? = null,

        val expirationDate: LocalDate? = null,

        @field:Size(max = 500, message = "As observações do item devem ter no máximo 500 caracteres")
        val notes: String? = null,

        val createdAt: LocalDateTime? = null,

        val updatedAt: LocalDateTime? = null
    ) {

        companion object {

            fun fromEntity(orderItem: edu.fatec.petwise.domain.entity.OrderItem): OrderItemDTO {
                return OrderItemDTO(
                    id = orderItem.id,
                    medicationId = orderItem.pharmacyOrder.id, // Ajustar conforme necessário
                    medicationName = orderItem.medicationName,
                    genericName = orderItem.genericName,
                    quantity = orderItem.quantity,
                    unitPrice = orderItem.unitPrice,
                    totalPrice = orderItem.totalPrice,
                    batchNumber = orderItem.batchNumber,
                    expirationDate = orderItem.expirationDate,
                    notes = orderItem.notes,
                    createdAt = orderItem.createdAt,
                    updatedAt = orderItem.updatedAt
                )
            }

            fun toEntity(orderItemDTO: OrderItemDTO, pharmacyOrder: PharmacyOrder): edu.fatec.petwise.domain.entity.OrderItem {
                return edu.fatec.petwise.domain.entity.OrderItem(
                    id = orderItemDTO.id,
                    pharmacyOrder = pharmacyOrder,
                    medicationName = orderItemDTO.medicationName,
                    genericName = orderItemDTO.genericName,
                    quantity = orderItemDTO.quantity,
                    unitPrice = orderItemDTO.unitPrice,
                    totalPrice = orderItemDTO.totalPrice,
                    batchNumber = orderItemDTO.batchNumber,
                    expirationDate = orderItemDTO.expirationDate,
                    notes = orderItemDTO.notes,
                    createdAt = orderItemDTO.createdAt ?: LocalDateTime.now(),
                    updatedAt = orderItemDTO.updatedAt ?: LocalDateTime.now()
                )
            }
        }

        init {
            val calculatedTotal = unitPrice.multiply(BigDecimal(quantity))
            if (totalPrice != calculatedTotal) {
                throw IllegalArgumentException(
                    "Preço total inválido. Esperado: $calculatedTotal, Informado: $totalPrice"
                )
            }
        }
    }

    companion object {

        fun fromEntity(pharmacyOrder: PharmacyOrder): PharmacyOrderDTO {
            return PharmacyOrderDTO(
                id = pharmacyOrder.id,
                orderNumber = pharmacyOrder.orderNumber,
                supplierId = pharmacyOrder.supplierId,
                orderDate = pharmacyOrder.orderDate,
                expectedDeliveryDate = pharmacyOrder.expectedDeliveryDate,
                actualDeliveryDate = pharmacyOrder.actualDeliveryDate,
                status = pharmacyOrder.status,
                totalAmount = pharmacyOrder.totalAmount,
                notes = pharmacyOrder.notes,
                createdAt = pharmacyOrder.createdAt,
                updatedAt = pharmacyOrder.updatedAt,
                orderItems = pharmacyOrder.orderItems.map { OrderItemDTO.fromEntity(it) }
            )
        }

        fun toEntity(pharmacyOrderDTO: PharmacyOrderDTO): PharmacyOrder {
            val order = PharmacyOrder(
                id = pharmacyOrderDTO.id ?: UUID.randomUUID(),
                orderNumber = pharmacyOrderDTO.orderNumber,
                supplierId = pharmacyOrderDTO.supplierId,
                orderDate = pharmacyOrderDTO.orderDate,
                expectedDeliveryDate = pharmacyOrderDTO.expectedDeliveryDate,
                actualDeliveryDate = pharmacyOrderDTO.actualDeliveryDate,
                status = pharmacyOrderDTO.status,
                totalAmount = pharmacyOrderDTO.totalAmount,
                notes = pharmacyOrderDTO.notes,
                createdAt = pharmacyOrderDTO.createdAt ?: LocalDateTime.now(),
                updatedAt = pharmacyOrderDTO.updatedAt ?: LocalDateTime.now()
            )
            val items = pharmacyOrderDTO.orderItems?.map {
                OrderItemDTO.toEntity(it, order)
            }?.toMutableList() ?: mutableListOf()

            return order.copy(orderItems = items)
        }


            }
        }

