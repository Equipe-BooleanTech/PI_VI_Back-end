package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Sale
import edu.fatec.petwise.domain.enums.PaymentMethod
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

@Repository
interface SaleRepository : JpaRepository<Sale, UUID> {
    fun findByPaymentMethod(paymentMethod: PaymentMethod, pageable: Pageable): Page<Sale>
    fun findBySaleDateBetween(startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<Sale>
    fun findBySaleDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Sale>
    fun findByMedicationNameContainingIgnoreCase(medicationName: String, pageable: Pageable): Page<Sale>
    fun findBySaleNumber(saleNumber: String): Optional<Sale>
    fun existsBySaleNumber(saleNumber: String): Boolean
    fun findByCustomerNameContainingIgnoreCaseOrCustomerPhone(
        customerName: String, 
        customerPhone: String, 
        pageable: Pageable
    ): Page<Sale>
    @Query("SELECT s FROM Sale s WHERE s.isPrescriptionRequired = true AND (s.prescriptionNumber IS NULL OR s.prescriptionNumber = '')")
    fun findSalesWithoutPrescription(pageable: Pageable): Page<Sale>
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    fun sumTotalAmountBySaleDateBetween(
        @Param("startDate") startDate: LocalDateTime, 
        @Param("endDate") endDate: LocalDateTime
    ): java.math.BigDecimal
    @Query("SELECT s.paymentMethod, COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate GROUP BY s.paymentMethod")
    fun countByPaymentMethodAndSaleDateBetween(
        @Param("startDate") startDate: LocalDateTime, 
        @Param("endDate") endDate: LocalDateTime
    ): List<Array<Any>>
}
