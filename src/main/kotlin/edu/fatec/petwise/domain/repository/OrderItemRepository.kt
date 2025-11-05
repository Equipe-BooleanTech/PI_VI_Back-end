package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.OrderItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Repository
interface OrderItemRepository : JpaRepository<OrderItem, UUID> {

    fun findByPharmacyOrderId(orderId: UUID): List<OrderItem>
    fun findByMedicationNameContainingIgnoreCase(name: String): List<OrderItem>
    fun findByBatchNumber(batchNumber: String): List<OrderItem>
    fun findByExpirationDateBefore(date: LocalDate): List<OrderItem>
    fun findByPharmacyOrderIdAndMedicationName(
        orderId: UUID,
        medicationName: String
    ): List<OrderItem>
    @Query(
        """
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM OrderItem oi
        WHERE oi.pharmacyOrder.id = :orderId
        """
    )
    fun getTotalQuantityByOrderId(@Param("orderId") orderId: UUID): Long

    @Query(
        """
        SELECT COALESCE(SUM(oi.totalPrice), 0.0)
        FROM OrderItem oi
        WHERE oi.pharmacyOrder.id = :orderId
        """
    )
    fun getTotalValueByOrderId(@Param("orderId") orderId: UUID): BigDecimal
    @Query(
        """
        SELECT oi FROM OrderItem oi
        WHERE (:orderId IS NULL OR oi.pharmacyOrder.id = :orderId)
        AND (:medicationName IS NULL OR LOWER(oi.medicationName) LIKE LOWER(CONCAT('%', :medicationName, '%')))
        AND (:batchNumber IS NULL OR oi.batchNumber = :batchNumber)
        AND (:expirationDate IS NULL OR oi.expirationDate <= :expirationDate)
        ORDER BY oi.createdAt DESC
        """
    )
    fun findWithFilters(
        @Param("orderId") orderId: UUID? = null,
        @Param("medicationName") medicationName: String? = null,
        @Param("batchNumber") batchNumber: String? = null,
        @Param("expirationDate") expirationDate: LocalDate? = null
    ): List<OrderItem>

    @Query(
        """
        SELECT oi FROM OrderItem oi
        WHERE oi.expirationDate = :date
        ORDER BY oi.expirationDate, oi.medicationName
        """
    )
    fun findByExpirationDate(@Param("date") date: LocalDate): List<OrderItem>

    @Query(
        """
        SELECT COUNT(DISTINCT oi.medicationName)
        FROM OrderItem oi
        WHERE oi.pharmacyOrder.id = :orderId
        """
    )
    fun countDistinctMedicationsByOrderId(@Param("orderId") orderId: UUID): Long

    @Query(
        """
        SELECT oi FROM OrderItem oi
        WHERE oi.expirationDate < :today
        ORDER BY oi.expirationDate ASC
        """
    )
    fun findExpiredItems(@Param("today") today: LocalDate): List<OrderItem>

    @Query(
        """
        SELECT oi FROM OrderItem oi
        WHERE oi.expirationDate BETWEEN :today AND :futureDate
        ORDER BY oi.expirationDate ASC
        """
    )
    fun findNearExpirationItems(
        @Param("today") today: LocalDate,
        @Param("futureDate") futureDate: LocalDate
    ): List<OrderItem>
}