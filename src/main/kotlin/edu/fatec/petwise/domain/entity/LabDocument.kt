package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID



@Entity
@Table(name = "lab_documents")
data class LabDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "file_name", nullable = false)
    @NotNull
    @Size(max = 255)
    val fileName: String,

    @Column(name = "original_file_name", nullable = false)
    @NotNull
    @Size(max = 255)
    val originalFileName: String,

    @Column(name = "file_type", nullable = false)
    @NotNull
    @Size(max = 100)
    val fileType: String,

    @Column(name = "file_size", nullable = false)
    @NotNull
    val fileSize: Long,

    @Column(name = "file_path", nullable = false)
    @NotNull
    @Size(max = 500)
    val filePath: String,

    @Column(name = "document_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    val documentType: String,

    @Column(name = "result_id")
    val resultId: UUID? = null,

    @Column(name = "sample_id")
    val sampleId: UUID? = null,

    @Column(name = "pet_id")
    val petId: UUID? = null,

    @Column(name = "uploaded_by")
    val uploadedBy: UUID? = null,

    @Column(name = "upload_date", nullable = false)
    @NotNull
    val uploadDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_encrypted", nullable = false)
    @NotNull
    val isEncrypted: Boolean = false,

    @Column(name = "description")
    @Size(max = 1000)
    val description: String? = null,

    @Column(name = "tags")
    @Size(max = 500)
    val tags: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    @NotNull
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    @NotNull
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", insertable = false, updatable = false)
    val labResult: LabResult? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", insertable = false, updatable = false)
    val labSample: LabSample? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", insertable = false, updatable = false)
    val pet: Pet? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    val uploadedByVeterinarian: User
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}