package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.LabTest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

@Repository
interface LabTestRepository : JpaRepository<LabTest, UUID> {
    
    fun findByCategory(category: String): List<LabTest>
    
    fun findBySampleType(sampleType: String): List<LabTest>
    
    fun findByIsActiveTrueOrderByTestName(): List<LabTest>
    
    fun findByTestNameContainingIgnoreCase(testName: String): List<LabTest>
    
    fun findByTestCode(testCode: String): Optional<LabTest>
    
    fun findByCategoryAndIsActiveTrue(category: String): List<LabTest>
    
    fun findByPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<LabTest>
    
    fun countByCategory(category: String): Long
    
    @Query("SELECT lt FROM LabTest lt ORDER BY (SELECT COUNT(ord) FROM Order ord WHERE ord.labTest = lt) DESC")
    fun findMostPopularTests(): List<LabTest>
    
    @Query("SELECT new map(lt.category as category, COUNT(lt) as testCount, AVG(lt.price) as avgPrice) " +
           "FROM LabTest lt " +
           "WHERE lt.isActive = true " +
           "GROUP BY lt.category " +
           "ORDER BY testCount DESC")
    fun findCategoryStatistics(): List<Map<String, Any>>
}