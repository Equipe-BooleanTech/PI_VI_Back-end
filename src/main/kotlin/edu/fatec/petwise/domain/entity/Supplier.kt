package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "suppliers")
data class Supplier(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "company_name", nullable = false, length = 255)
    var companyName: String,

    @Column(name = "trade_name", length = 255)
    var tradeName: String? = null,

    @Column(name = "cnpj", unique = true, length = 18)
    val cnpj: String? = null,

    @Column(name = "cpf", length = 14)
    val cpf: String? = null,

    @Column(name = "contact_person", length = 255)
    val contactPerson: String? = null,

    @Column(name = "email", length = 255)
    var email: String? = null,

    @Column(name = "phone", length = 20)
    var phone: String? = null,

    @Column(name = "alternative_phone", length = 20)
    val alternativePhone: String? = null,

    @Column(columnDefinition = "TEXT")
    var address: String? = null,

    @Column(length = 100)
    var city: String? = null,

    @Column(length = 2)
    var state: String? = null,

    @Column(name = "zip_code", length = 10)
    var zipCode: String? = null,

    @Column(length = 100)
    val neighborhood: String? = null,

    @Column(length = 255)
    val website: String? = null,

    @Column(name = "payment_terms", length = 255)
    val paymentTerms: String? = null,

    @Column(name = "credit_limit", precision = 12, scale = 2)
    val creditLimit: BigDecimal = BigDecimal.ZERO,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column
    var rating: Int? = null,

    @Column(columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
