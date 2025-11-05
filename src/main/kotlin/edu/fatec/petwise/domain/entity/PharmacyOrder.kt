package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.PharmacyOrderStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "pharmacy_orders")
data class PharmacyOrder(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "order_number", unique = true, nullable = false, length = 50)
    val orderNumber: String,

    @Column(name = "supplier_id", nullable = false)
    val supplierId: UUID,

    @Column(name = "order_date", nullable = false)
    val orderDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "expected_delivery_date")
    val expectedDeliveryDate: LocalDate? = null,

    @Column(name = "actual_delivery_date")
    val actualDeliveryDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: PharmacyOrderStatus = PharmacyOrderStatus.PENDING,

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @Column(columnDefinition = "TEXT")
    var notes: String? = null,

    @OneToMany(mappedBy = "pharmacyOrder", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var orderItems: MutableList<OrderItem> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
