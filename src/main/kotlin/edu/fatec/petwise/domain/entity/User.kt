package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID? = null,
    var fullName: String,
    val email: Email,
    var phone: Telefone,
    var passwordHash: String,
    val userType: UserType,
    val cpf: String? = null,
    val crmv: String? = null,
    val specialization: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(fullName.isNotBlank()) { "Nome completo não pode estar vazio" }
        
        when (userType) {
            UserType.OWNER -> {
                require(!cpf.isNullOrBlank()) { "CPF é obrigatório para tutores" }
                require(validateCpf(cpf)) { "CPF inválido" }
            }
            UserType.VETERINARY -> {
                require(!crmv.isNullOrBlank()) { "CRMV é obrigatório para veterinários" }
                require(!specialization.isNullOrBlank()) { "Especialização é obrigatória para veterinários" }
            }
            UserType.PHARMACY -> {
                require(!cnpj.isNullOrBlank()) { "CNPJ é obrigatório para farmácias" }
                require(!companyName.isNullOrBlank()) { "Nome da empresa é obrigatório para farmácias" }
                require(validateCnpj(cnpj)) { "CNPJ inválido" }
            }
            UserType.ADMIN -> {}
        }
    }

    fun deactivate(): User = this.copy(active = false, updatedAt = LocalDateTime.now())

    fun update(
        fullName: String? = null,
        phone: Telefone? = null,
        specialization: String? = null,
        companyName: String? = null
    ): User = this.copy(
        fullName = fullName ?: this.fullName,
        phone = phone ?: this.phone,
        specialization = specialization ?: this.specialization,
        companyName = companyName ?: this.companyName,
        updatedAt = LocalDateTime.now()
    )

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
}
