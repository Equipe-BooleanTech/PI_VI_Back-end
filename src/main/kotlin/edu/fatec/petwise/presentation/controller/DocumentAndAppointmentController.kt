package edu.fatec.petwise.presentation.controller



import edu.fatec.petwise.application.dto.AppointmentResponse
import edu.fatec.petwise.application.dto.DocumentUploadResponse
import edu.fatec.petwise.application.usecase.UpdateAppointmentStatusUseCase
import edu.fatec.petwise.application.usecase.UploadDocumentUseCase
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping
@CrossOrigin(origins = ["*"])
class DocumentAndAppointmentController(
    private val updateAppointmentStatusUseCase: UpdateAppointmentStatusUseCase,
    private val uploadDocumentUseCase: UploadDocumentUseCase
) {

    @PutMapping("/appointments/{id}/status")
    fun updateAppointmentStatus(
        @PathVariable id: UUID,
        @RequestParam status: String,
        authentication: Authentication
    ): ResponseEntity<AppointmentResponse> {
        val updatedAppointment = updateAppointmentStatusUseCase.execute(id, status, authentication)
        return ResponseEntity.ok(updatedAppointment)
    }

    @PostMapping("/uploads/medical-document")
    fun uploadMedicalDocument(
        @RequestParam file: MultipartFile,
        @RequestParam documentType: String,
        @RequestParam(required = false) description: String?,
        @RequestParam(required = false) petId: UUID?,
        @RequestParam(required = false) medicalRecordId: UUID?,
        @RequestParam(required = false) appointmentId: UUID?,
        authentication: Authentication
    ): ResponseEntity<DocumentUploadResponse> {
        val document = uploadDocumentUseCase.execute(
            file, documentType, authentication, description, petId, medicalRecordId, appointmentId
        )
        return ResponseEntity.ok(document)
    }
}