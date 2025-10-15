package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.ConsultaType
import edu.fatec.petwise.domain.enums.ConsultaStatus
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "appointments")
class AppointmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, name = "pet_id")
    var petId: UUID,
    
    @Column(nullable = false, name = "owner_id")
    var ownerId: UUID,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "consulta_type")
    var consultaType: ConsultaType,
    
    @Column(nullable = false, name = "consulta_date")
    var consultaDate: String,
    
    @Column(nullable = false, name = "consulta_time")
    var consultaTime: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ConsultaStatus = ConsultaStatus.SCHEDULED,
    
    @Column(columnDefinition = "TEXT")
    var symptoms: String = "",
    
    @Column(columnDefinition = "TEXT")
    var diagnosis: String = "",
    
    @Column(columnDefinition = "TEXT")
    var treatment: String = "",
    
    @Column(columnDefinition = "TEXT")
    var prescriptions: String = "",
    
    @Column(columnDefinition = "TEXT")
    var notes: String = "",
    
    @Column(name = "next_appointment")
    var nextAppointment: String? = null,
    
    @Column(nullable = false)
    var price: Float = 0f,
    
    @Column(nullable = false, name = "is_paid")
    var isPaid: Boolean = false,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
