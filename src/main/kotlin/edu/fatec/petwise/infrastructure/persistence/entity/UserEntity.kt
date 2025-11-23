package edu.fatec.petwise.infrastructure.persistence.entity

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "full_name")
    var fullName: String = "",

    @Column(name = "email")
    var email: String = "",

    @Column(name = "phone")
    var phone: String = "",

    @Column(name = "password_hash")
    var passwordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    var userType: UserType = UserType.OWNER,

    var cpf: String? = null,

    var crmv: String? = null,

    var specialization: String? = null,

    var cnpj: String? = null,

    @Column(name = "company_name")
    var companyName: String? = null,

    var active: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    constructor(): this(null)
}
