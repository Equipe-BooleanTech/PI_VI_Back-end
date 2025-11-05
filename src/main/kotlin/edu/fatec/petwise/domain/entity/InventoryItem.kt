package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "inventory_items")
data class InventoryItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: UUID? = null,

    @Column(name = "medication_name", nullable = false)
    @field:NotBlank(message = "Medication name is required")
    var medicationName: String,

    @Column(name = "generic_name")
    var genericName: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "current_stock")
    @field:Min(value = 0, message = "Current stock must be non-negative")
    var currentStock: Int = 0,

    @Column(name = "minimum_stock")
    @field:Min(value = 0, message = "Minimum stock must be non-negative")
    var minimumStock: Int = 0,

    @Column(name = "maximum_stock")
    @field:Min(value = 0, message = "Maximum stock must be non-negative")
    var maximumStock: Int = 0,

    @Column(name = "unit_price", precision = 10, scale = 2)
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be positive")
    var unitPrice: BigDecimal? = null,

    @Column(name = "supplier_id")
    @field:Positive(message = "Supplier ID must be positive")
    var supplierId: UUID? = null,

    @Column(name = "batch_number")
    var batchNumber: String? = null,

    @Column(name = "expiration_date")
    @field:Future(message = "Expiration date must be in the future")
    var expirationDate: LocalDate? = null,

    var location: String? = null,

    @Column(name = "is_active")
    var isActive: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null
) {
    
    @PrePersist
    protected fun onCreate() {
        val now = LocalDateTime.now()
        createdAt = now
        updatedAt = now
    }
    
    @PreUpdate
    protected fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}