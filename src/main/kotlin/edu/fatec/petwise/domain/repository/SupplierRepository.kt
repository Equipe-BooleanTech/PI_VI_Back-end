package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Supplier
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.UUID

@Repository
interface SupplierRepository : JpaRepository<Supplier, UUID> {
fun findByEmail (email: String): Supplier?
    fun findByIsActiveTrue(pageable: Pageable): Page<Supplier>
    fun findByCnpj(cnpj: String): Supplier?
    fun findByCompanyNameContainingIgnoreCase(name: String, pageable: Pageable): Page<Supplier>
    fun findByStateAndCity(state: String, city: String, pageable: Pageable): Page<Supplier>

    /*@Query("""
    SELECT DISTINCT s FROM Supplier s
    INNER JOIN s.orders o
        WHERE o.status IN ('PENDING', 'PROCESSING', 'SHIPPED') 
        AND s.isActive = true
        ORDER BY s.companyName
    """)
    fun findSuppliersWithActiveOrders(pageable: Pageable): Page<Supplier>*/
    fun findByRatingGreaterThanEqual(rating: Int, pageable: Pageable): Page<Supplier>
    fun findByCreditLimitGreaterThan(limit: BigDecimal, pageable: Pageable): Page<Supplier>

    @Query(
        """
        SELECT s FROM Supplier s 
        WHERE LOWER(s.companyName) LIKE LOWER(CONCAT('%', :criteria, '%'))
        OR LOWER(s.tradeName) LIKE LOWER(CONCAT('%', :criteria, '%'))
        OR LOWER(s.contactPerson) LIKE LOWER(CONCAT('%', :criteria, '%'))
        OR LOWER(s.email) LIKE LOWER(CONCAT('%', :criteria, '%'))
        OR s.cnpj LIKE CONCAT('%', :criteria, '%')
        ORDER BY s.companyName
    """
    )
    fun searchSuppliers(@Param("criteria") criteria: String, pageable: Pageable): Page<Supplier>

    @Query("SELECT s FROM Supplier s WHERE s.state = :state AND s.isActive = true ORDER BY s.companyName")
    fun findActiveSuppliersByState(@Param("state") state: String, pageable: Pageable): Page<Supplier>

    @Query("SELECT s FROM Supplier s WHERE s.rating = :rating AND s.isActive = true ORDER BY s.companyName")
    fun findSuppliersByRating(@Param("rating") rating: Int, pageable: Pageable): Page<Supplier>

    @Query(
        """
        SELECT s FROM Supplier s 
        WHERE s.creditLimit BETWEEN :minLimit AND :maxLimit 
        AND s.isActive = true 
        ORDER BY s.creditLimit DESC
    """
    )
    fun findSuppliersByCreditLimitRange(
        @Param("minLimit") minLimit: BigDecimal,
        @Param("maxLimit") maxLimit: BigDecimal,
        pageable: Pageable
    ): Page<Supplier>

    fun findByCity(city: String, pageable: Pageable): Page<Supplier>

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.state = :state AND s.isActive = true")
    fun countActiveSuppliersByState(@Param("state") state: String): Long

}
