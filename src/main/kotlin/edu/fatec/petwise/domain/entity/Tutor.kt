package edu.fatec.petwise.domain.entity

import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import java.time.LocalDateTime
import java.util.UUID

data class Tutor(
    val id: UUID? = null,
    val name: String,
    val cpf: String,
    val email: Email,
    val phone: Telefone,
    val address: String?,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    init {
        require(name.isNotBlank()) { "Nome do tutor não pode estar vazio" }
        require(cpf.isNotBlank()) { "CPF não pode estar vazio" }
        require(validateCpf(cpf)) { "CPF inválido" }
    }

    fun deactivate(): Tutor = this.copy(active = false, updatedAt = LocalDateTime.now())

    fun update(
        name: String? = null,
        phone: Telefone? = null,
        address: String? = null
    ): Tutor = this.copy(
        name = name ?: this.name,
        phone = phone ?: this.phone,
        address = address ?: this.address,
        updatedAt = LocalDateTime.now()
    )

    companion object {
        private fun validateCpf(cpf: String): Boolean {
            val digitsOnly = cpf.replace(Regex("[^0-9]"), "")
            if (digitsOnly.length != 11) return false
            if (digitsOnly.all { it == digitsOnly[0] }) return false
            
            val digits = digitsOnly.map { it.toString().toInt() }
            
            val firstDigit = calculateDigit(digits.subList(0, 9))
            if (firstDigit != digits[9]) return false
            
            val secondDigit = calculateDigit(digits.subList(0, 10))
            return secondDigit == digits[10]
        }

        private fun calculateDigit(digits: List<Int>): Int {
            val weight = digits.size + 1
            val sum = digits.mapIndexed { index, digit -> 
                digit * (weight - index) 
            }.sum()
            val remainder = sum % 11
            return if (remainder < 2) 0 else 11 - remainder
        }
    }
}
