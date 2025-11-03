package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "documents")
data class Document(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID,
    
    @Column(name = "user_id", nullable = false)
    val userId: UUID,
    
    @Column(name = "pet_id", nullable = false)
    val petId: UUID,
    
    @Column(name = "medical_record_id")
    val medicalRecordId: UUID?,
    
    @Column(name = "appointment_id")
    val appointmentId: UUID?,
    
    @Column(name = "document_name", nullable = false, length = 200)
    val documentName: String,
    
    @Column(name = "document_type", nullable = false, length = 50)
    val documentType: String,
    
    @Column(name = "file_path", nullable = false)
    val filePath: String,
    
    @Column(name = "file_size_bytes")
    val fileSizeBytes: Long? = null,
    
    @Column(name = "mime_type", length = 100)
    val mimeType: String? = null,
    
    @Column(name = "upload_date", nullable = false)
    val uploadDate: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,
    
    @Column(name = "active", nullable = false)
    val active: Boolean = true,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)