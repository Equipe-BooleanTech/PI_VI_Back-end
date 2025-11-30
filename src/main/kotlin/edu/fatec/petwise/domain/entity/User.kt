package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import java.time.LocalDateTime
import java.util.UUID

class User(
    val id: UUID? = null,

    var fullName: String,

    var email: Email,

    var phone: Telefone,

    var passwordHash: String,

    val userType: UserType,

    var cpf: String? = null,

    var crmv: String? = null,

    var specialization: String? = null,

    var cnpj: String? = null,

    var companyName: String? = null,

    var active: Boolean = true,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    
    constructor(): this(
        id = null,
        fullName = "",
        email = edu.fatec.petwise.domain.valueobject.Email(""),
        phone = edu.fatec.petwise.domain.valueobject.Telefone(""),
        passwordHash = "",
        userType = UserType.OWNER,
        cpf = null,
        crmv = null,
        specialization = null,
        cnpj = null,
        companyName = null,
        active = true,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    fun deactivate(): User {
        this.active = false
        this.updatedAt = LocalDateTime.now()
        return this
    }

    fun update(
        fullName: String? = null,
        phone: Telefone? = null,
        specialization: String? = null,
        companyName: String? = null
    ): User {
        fullName?.let { this.fullName = it }
        phone?.let { this.phone = it }
        specialization?.let { this.specialization = it }
        companyName?.let { this.companyName = it }
        this.updatedAt = LocalDateTime.now()
        return this
    }

    fun isOwner() = userType == UserType.OWNER
    fun isVeterinary() = userType == UserType.VETERINARY
    fun isPharmacy() = userType == UserType.PHARMACY
    fun isAdmin() = userType == UserType.ADMIN

    companion object {
        private fun validateCpf(cpf: String): Boolean {
            val digitsOnly = cpf.replace(Regex("[^0-9]"), "")
            if (digitsOnly.length != 11) return false
            if (digitsOnly.all { it == digitsOnly[0] }) return false
            
            val digits = digitsOnly.map { it.toString().toInt() }
            
            val firstDigit = calculateCpfDigit(digits.subList(0, 9))
            if (firstDigit != digits[9]) return false
            
            val secondDigit = calculateCpfDigit(digits.subList(0, 10))
            return secondDigit == digits[10]
        }

        private fun calculateCpfDigit(digits: List<Int>): Int {
            val weight = digits.size + 1
            val sum = digits.mapIndexed { index, digit -> 
                digit * (weight - index) 
            }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }

        private fun validateCnpj(cnpj: String): Boolean {
            val digitsOnly = cnpj.replace(Regex("[^0-9]"), "")
            if (digitsOnly.length != 14) return false
            if (digitsOnly.all { it == digitsOnly[0] }) return false
            
            val digits = digitsOnly.map { it.toString().toInt() }
            
            val firstDigit = calculateCnpjDigit(digits.subList(0, 12), intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2))
            if (firstDigit != digits[12]) return false
            
            val secondDigit = calculateCnpjDigit(digits.subList(0, 13), intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2))
            return secondDigit == digits[13]
        }

        private fun calculateCnpjDigit(digits: List<Int>, weights: IntArray): Int {
            val sum = digits.mapIndexed { index, digit -> 
                digit * weights[index] 
            }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }
    }

    fun toResponse(): edu.fatec.petwise.application.dto.UserResponse = edu.fatec.petwise.application.dto.UserResponse.fromEntity(this)
}
