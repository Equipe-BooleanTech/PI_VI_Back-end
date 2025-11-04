package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.PharmacyOrder
import edu.fatec.petwise.domain.enums.PharmacyOrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface PharmacyOrderRepository : JpaRepository<PharmacyOrder, UUID> {

    fun findByStatus(status: PharmacyOrderStatus): List<PharmacyOrder>

    fun findBySupplierId(supplierId: UUID): List<PharmacyOrder>

    fun findByOrderDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<PharmacyOrder>

    fun findByStatusAndOrderDateBetween(
        status: PharmacyOrderStatus,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<PharmacyOrder>

    @Query("SELECT po FROM PharmacyOrder po WHERE po.status = edu.fatec.petwise.domain.enums.PharmacyOrderStatus.PENDING")
    fun findPendingOrders(): List<PharmacyOrder>

    fun findOrdersBySupplierIdAndStatus(
        supplierId: UUID,
        status: PharmacyOrderStatus,
        pageable: Pageable
    ): Page<PharmacyOrder>

    @Query("SELECT COALESCE(SUM(po.totalAmount), 0) FROM PharmacyOrder po WHERE po.status = ?1")
    fun getTotalAmountByStatus(status: PharmacyOrderStatus): BigDecimal

    @Query("SELECT po FROM PharmacyOrder po WHERE po.totalAmount >= ?1 ORDER BY po.totalAmount DESC")
    fun findOrdersWithMinAmount(minAmount: BigDecimal, pageable: Pageable): Page<PharmacyOrder>

    @Query("SELECT COUNT(po) FROM PharmacyOrder po WHERE po.supplierId = ?1")
    fun countOrdersBySupplierId(supplierId: UUID): Long

    @Query("SELECT po FROM PharmacyOrder po WHERE po.supplierId = ?1 ORDER BY po.orderDate DESC")
    fun findRecentOrdersBySupplierId(supplierId: UUID): List<PharmacyOrder>

    @Query("SELECT COALESCE(AVG(po.totalAmount), 0) FROM PharmacyOrder po WHERE po.orderDate BETWEEN ?1 AND ?2")
    fun getAverageOrderAmount(startDate: LocalDateTime, endDate: LocalDateTime): BigDecimal

    @Query("SELECT po FROM PharmacyOrder po WHERE po.status IN ?1 ORDER BY po.orderDate DESC")
    fun findByStatusIn(statuses: List<PharmacyOrderStatus>, pageable: Pageable): Page<PharmacyOrder>
}
