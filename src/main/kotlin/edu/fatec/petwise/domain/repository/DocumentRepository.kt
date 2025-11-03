package edu.fatec.petwise.domain.repository


import edu.fatec.petwise.domain.entity.Document
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface DocumentRepository : JpaRepository<Document, Long> {
    
    fun findByPetIdAndActiveTrueOrderByUploadDateDesc(petId: UUID): List<Document>
    
    fun findByUserIdAndActiveTrue(userId: UUID): List<Document>
    
    fun findByIdAndUserIdAndActiveTrue(id: UUID, userId: UUID): Document?
    
    fun findByPetIdAndDocumentTypeAndActiveTrue(petId: UUID, documentType: String): List<Document>
    
    fun findByMedicalRecordIdAndActiveTrue(medicalRecordId: UUID): List<Document>
    
    fun findByAppointmentIdAndActiveTrue(appointmentId: UUID): List<Document>
    
    @Query("SELECT d FROM Document d WHERE d.petId = :petId AND (d.documentType LIKE %:documentType%) AND d.active = true ORDER BY d.uploadDate DESC")
    fun findByPetIdAndDocumentTypeContaining(@Param("petId") petId: Long, @Param("documentType") documentType: String): List<Document>
    
    @Query("SELECT d FROM Document d WHERE d.userId = :userId AND d.uploadDate BETWEEN :startDate AND :endDate AND d.active = true ORDER BY d.uploadDate DESC")
    fun findByUserIdAndUploadDateBetween(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Document>
    
    @Query("SELECT COUNT(d) FROM Document d WHERE d.petId = :petId AND d.active = true")
    fun countByPetIdAndActiveTrue(@Param("petId") petId: Long): Long
}