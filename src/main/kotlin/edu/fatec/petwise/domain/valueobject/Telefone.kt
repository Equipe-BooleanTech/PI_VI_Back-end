package edu.fatec.petwise.domain.valueobject

data class Telefone(val value: String) {
    init {
        require(value.isNotBlank()) { "Telefone não pode estar vazio" }
        require(validateFormat(value)) { "Formato de telefone inválido" }
    }

    companion object {
        private val TELEFONE_REGEX = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$".toRegex()

        private fun validateFormat(phone: String): Boolean {
            return TELEFONE_REGEX.matches(phone)
        }
    }

    fun formatted(): String {
        val digits = value.replace(Regex("[^0-9]"), "")
        return if (digits.length == 11) {
            "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
        } else {
            "(${digits.substring(0, 2)}) ${digits.substring(2, 6)}-${digits.substring(6)}"
        }
    }

    override fun toString(): String = value
}
