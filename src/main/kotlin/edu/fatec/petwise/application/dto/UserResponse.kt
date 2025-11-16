package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.User
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val fullName: String,
    val email: String,
    val phone: String,
    val userType: String,
    val cpf: String?,
    val crmv: String?,
    val specialization: String?,
    val cnpj: String?,
    val companyName: String?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)

{
    companion object {
        fun fromEntity(user: User): UserResponse =
            UserResponse(
                id = (user.id ?: "") as UUID,
                fullName = user.fullName,
                email = user.email.value,
                phone = user.phone.value,
                userType = user.userType.name,
                cpf = user.cpf,
                crmv = user.crmv,
                specialization = user.specialization,
                cnpj = user.cnpj,
                companyName = user.companyName,
                active = user.active,
                createdAt = user.createdAt.toString(),
                updatedAt = user.updatedAt.toString()
            )
    }
}
