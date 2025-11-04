package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.PaymentMethod
import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "sales")
data class Sale(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,

    @Column(name = "sale_number", unique = true, nullable = false)
    @field:NotBlank(message = "Sale number is required")
    var saleNumber: String,

    @Column(name = "customer_name")
    val customerName: String? = null,

    @Column(name = "customer_phone")
    val customerPhone: String? = null,

    @Column(name = "veterinarian_name")
    val veterinarianName: String? = null,

    @Column(name = "medication_name", nullable = false)
    @field:NotBlank(message = "Medication name is required")
    val medicationName: String,

    @Column(name = "quantity", nullable = false)
    @field:Min(value = 1, message = "Quantity must be at least 1")
    val quantity: Int,

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be positive")
    val unitPrice: BigDecimal,

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
    val totalAmount: BigDecimal,

    @Column(name = "sale_date", nullable = false)
    @field:NotNull(message = "Sale date is required")
    val saleDate: LocalDateTime,

    @Column(name = "prescription_number")
    val prescriptionNumber: String? = null,

    @Column(name = "batch_number")
    val batchNumber: String? = null,

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    val paymentMethod: PaymentMethod,

    @Column(name = "is_prescription_required")
    val isPrescriptionRequired: Boolean = false,

    @Column(columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null
) {
    
    @PrePersist
    protected fun onCreate() {
        val now = LocalDateTime.now()
        createdAt = now
    }
}

