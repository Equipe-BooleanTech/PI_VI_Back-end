package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.LabDocument
import edu.fatec.petwise.domain.enums.DocumentType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
interface LabDocumentRepository : JpaRepository<LabDocument, UUID> {

    fun findByResultIdOrderByUploadDateDesc(resultId: UUID): List<LabDocument>
    
    fun findBySampleIdOrderByUploadDateDesc(sampleId: UUID): List<LabDocument>
    
    fun findByPetIdOrderByUploadDateDesc(petId: UUID): List<LabDocument>
    
    fun findByUploadedByOrderByUploadDateDesc(uploadedBy: UUID): List<LabDocument>
    
    fun findByDocumentType(documentType: DocumentType): List<LabDocument>
    
    fun findByFileNameContainingIgnoreCase(fileName: String): List<LabDocument>
    
    fun findByUploadDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<LabDocument>
    
    fun findByTagsContainingIgnoreCase(tags: String): List<LabDocument>
    
    fun countByDocumentType(documentType: DocumentType): Long
    
    fun findByIsEncryptedTrue(): List<LabDocument>
    
    @Query("SELECT d FROM LabDocument d WHERE d.pet.id = :petId AND d.uploadDate BETWEEN :startDate AND :endDate ORDER BY d.uploadDate DESC")
    fun findByPetIdAndUploadDateBetween(
        @Param("petId") petId: UUID,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<LabDocument>
    
    @Query("SELECT d FROM LabDocument d WHERE d.documentType = :documentType AND d.isEncrypted = :isEncrypted ORDER BY d.uploadDate DESC")
    fun findByDocumentTypeAndIsEncrypted(
        @Param("documentType") documentType: String,
        @Param("isEncrypted") isEncrypted: Boolean
    ): List<LabDocument>
}