package edu.fatec.petwise.domain.entity


import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "order_items")
data class OrderItem(
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,
    
    @NotNull(message = "O pedido é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_order_id", nullable = false)
    val pharmacyOrder: PharmacyOrder,
    
    @NotNull(message = "O nome do medicamento é obrigatório")
    @Column(name = "medication_name", nullable = false, length = 255)
    val medicationName: String,
    
    @Column(name = "generic_name", length = 255)
    val genericName: String? = null,
    
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser maior que zero")
    @Column(name = "quantity", nullable = false)
    val quantity: Int,
    
    @NotNull(message = "O preço unitário é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço unitário deve ser maior que zero")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    val unitPrice: BigDecimal,
    
    @NotNull(message = "O preço total é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preço total deve ser maior que zero")
    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    val totalPrice: BigDecimal,
    
    @Column(name = "batch_number", length = 100)
    val batchNumber: String? = null,
    
    @Column(name = "expiration_date")
    val expirationDate: java.time.LocalDate? = null,
    
    @Column(name = "notes", length = 500)
    val notes: String? = null,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    

    init {
        val calculatedTotal = unitPrice.multiply(BigDecimal(quantity))
        if (totalPrice != calculatedTotal) {
            throw IllegalArgumentException(
                "Preço total inválido. Esperado: $calculatedTotal, Informado: $totalPrice"
            )
        }
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}