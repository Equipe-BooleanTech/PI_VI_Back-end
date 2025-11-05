package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.InventoryItem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface InventoryRepository : JpaRepository<InventoryItem, UUID> {

    fun findByMedicationName(medicationName: String): Optional<InventoryItem>

    fun findByIsActiveTrue(): List<InventoryItem>


    fun findByIsActiveTrue(pageable: Pageable): Page<InventoryItem>

    @Query("SELECT i FROM InventoryItem i WHERE i.currentStock <= :minimumStock AND i.isActive = true")
    fun findByCurrentStockLessThanEqualAndIsActiveTrue(@Param("minimumStock") minimumStock: Int): List<InventoryItem>

    @Query("SELECT i FROM InventoryItem i WHERE i.expirationDate < :date AND i.isActive = true")
    fun findByExpirationDateBeforeAndIsActiveTrue(@Param("date") date: LocalDate): List<InventoryItem>

    @Query("SELECT i FROM InventoryItem i WHERE LOWER(i.medicationName) LIKE LOWER(CONCAT('%', :name, '%')) AND i.isActive = true")
    fun findByMedicationNameContainingIgnoreCase(@Param("name") name: String, pageable: Pageable): Page<InventoryItem>

    @Query("SELECT i FROM InventoryItem i WHERE i.currentStock <= i.minimumStock AND i.isActive = true")
    fun findLowStockItems(pageable: Pageable): Page<InventoryItem>

    @Query("""
        SELECT i FROM InventoryItem i 
        WHERE i.expirationDate BETWEEN :startDate AND :endDate 
        AND i.isActive = true
    """)
    fun findExpiringItems(
        @Param("startDate") startDate: LocalDate,
        @Param("endDate") endDate: LocalDate,
        pageable: Pageable
    ): Page<InventoryItem>

    fun countBySupplierId(supplierId: UUID): Long

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.supplierId = :supplierId AND i.isActive = true")
    fun countActiveBySupplierId(@Param("supplierId") supplierId: UUID): Long
}
