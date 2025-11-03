package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.entity.UserType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    
    @Column(nullable = false, name = "full_name", length = 100)
    var fullName: String,
    
    @Column(nullable = false, unique = true, length = 100)
    var email: String,
    
    @Column(nullable = false, length = 20)
    var phone: String,
    
    @Column(nullable = false, name = "password_hash")
    var passwordHash: String,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "user_type", length = 20)
    var userType: UserType,
    
    @Column(unique = true, length = 11)
    var cpf: String? = null,
    
    @Column(unique = true, length = 20)
    var crmv: String? = null,
    
    @Column(length = 100)
    var specialization: String? = null,
    
    @Column(unique = true, length = 14)
    var cnpj: String? = null,
    
    @Column(name = "company_name", length = 100)
    var companyName: String? = null,
    
    @Column(nullable = false)
    var active: Boolean = true,
    
    @Column(nullable = false, name = "created_at", updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(nullable = false, name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
