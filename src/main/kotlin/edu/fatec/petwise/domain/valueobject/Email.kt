package edu.fatec.petwise.domain.valueobject

data class Email(val value: String) {
    init {
        require(value.isNotBlank()) { "Email não pode estar vazio" }
        require(validateFormat(value)) { "Formato de email inválido" }
    }

    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        private fun validateFormat(email: String): Boolean {
            return EMAIL_REGEX.matches(email)
        }
    }

    override fun toString(): String = value
}
