package edu.fatec.petwise.application.dto

import edu.fatec.petwise.application.validation.SupplierValidation
import edu.fatec.petwise.domain.entity.Supplier
import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.NotBlank
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@SupplierValidation
data class SupplierDTO(
    val id: UUID? = null,

    @field:NotBlank(message = "Nome da empresa é obrigatório")
    @field:Size(max = 255, message = "Nome da empresa deve ter no máximo 255 caracteres")
    val companyName: String,

    @field:Size(max = 255, message = "Nome fantasia deve ter no máximo 255 caracteres")
    val tradeName: String? = null,

    @field:Pattern(regexp = CNPJ_PATTERN, message = "CNPJ deve estar em um formato válido")
    @field:Size(max = 18, message = "CNPJ deve ter no máximo 18 caracteres")
    val cnpj: String? = null,

    @field:Pattern(regexp = CPF_PATTERN, message = "CPF deve estar em um formato válido")
    @field:Size(max = 14, message = "CPF deve ter no máximo 14 caracteres")
    val cpf: String? = null,

    @field:Size(max = 255, message = "Pessoa de contato deve ter no máximo 255 caracteres")
    val contactPerson: String? = null,

    @field:Email(message = "Email deve estar em um formato válido")
    @field:Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    val email: String? = null,

    @field:Pattern(regexp = PHONE_PATTERN, message = "Telefone deve estar em um formato válido")
    val phone: String? = null,

    @field:Pattern(regexp = PHONE_PATTERN, message = "Telefone alternativo deve estar em um formato válido")
    val alternativePhone: String? = null,

    @field:Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    val address: String? = null,

    @field:Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    val city: String? = null,

    @field:Size(max = 2, message = "Estado deve ter no máximo 2 caracteres")
    val state: String? = null,

    @field:Pattern(regexp = ZIP_CODE_PATTERN, message = "CEP deve estar em um formato válido")
    @field:Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    val zipCode: String? = null,

    @field:Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
    val neighborhood: String? = null,

    @field:Pattern(regexp = URL_PATTERN, message = "Website deve estar em um formato válido")
    @field:Size(max = 255, message = "Website deve ter no máximo 255 caracteres")
    val website: String? = null,

    @field:Size(max = 255, message = "Termos de pagamento devem ter no máximo 255 caracteres")
    val paymentTerms: String? = null,

    @field:DecimalMin(value = "0.0", message = "Limite de crédito deve ser maior que zero")
    val creditLimit: BigDecimal? = null,

    val isActive: Boolean = true,

    @field:Min(value = 1, message = "Avaliação deve ser no mínimo 1")
    @field:Max(value = 5, message = "Avaliação deve ser no máximo 5")
    val rating: Int? = null,

    @field:Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    val notes: String? = null,

    val createdAt: LocalDateTime? = null,

    val updatedAt: LocalDateTime? = null
) {

    companion object {

        const val CNPJ_PATTERN = """^(\d{2}\.\d{3}\.\d{3}/\d{4}-\d{2}|\d{14})$"""

        const val CPF_PATTERN = """^(\d{3}\.\d{3}\.\d{3}-\d{2}|\d{11})$"""

        const val PHONE_PATTERN = """^(\+\d{1,3}\s?)?(\(?\d{2}\)?[\s.-]?)?\d{4,5}[\s.-]?\d{4}$"""

        const val ZIP_CODE_PATTERN = """^(\d{5}-\d{3}|\d{8})$"""

        const val URL_PATTERN = """^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$"""

        fun fromEntity(entity: Supplier): SupplierDTO {
            return SupplierDTO(
                id = entity.id,
                companyName = entity.companyName,
                tradeName = entity.tradeName,
                cnpj = entity.cnpj,
                cpf = entity.cpf,
                contactPerson = entity.contactPerson,
                email = entity.email,
                phone = entity.phone,
                alternativePhone = entity.alternativePhone,
                address = entity.address,
                city = entity.city,
                state = entity.state,
                zipCode = entity.zipCode,
                neighborhood = entity.neighborhood,
                website = entity.website,
                paymentTerms = entity.paymentTerms,
                creditLimit = entity.creditLimit,
                isActive = entity.isActive,
                rating = entity.rating,
                notes = entity.notes,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }

    init {
        val hasCnpj = !cnpj.isNullOrBlank()
        val hasCpf = !cpf.isNullOrBlank()

        when {
            !hasCnpj && !hasCpf -> {
                throw ValidationException("Pelo menos um documento (CNPJ ou CPF) deve ser fornecido")
            }
            hasCnpj && hasCpf -> {
                throw ValidationException("Apenas um documento (CNPJ ou CPF) deve ser fornecido, não ambos")
            }
        }
    }

    fun toEntity(): Supplier? {
        return this.creditLimit?.let {
            this.createdAt?.let { it1 ->
                this.updatedAt?.let { it2 ->
                    this.id?.let { it3 ->
                        Supplier(
                            id = it3,
                            companyName = this.companyName,
                            tradeName = this.tradeName,
                            cnpj = this.cnpj,
                            cpf = this.cpf,
                            contactPerson = this.contactPerson,
                            email = this.email,
                            phone = this.phone,
                            alternativePhone = this.alternativePhone,
                            address = this.address,
                            city = this.city,
                            state = this.state,
                            zipCode = this.zipCode,
                            neighborhood = this.neighborhood,
                            website = this.website,
                            paymentTerms = this.paymentTerms,
                            creditLimit = it,
                            isActive = this.isActive,
                            rating = this.rating,
                            notes = this.notes,
                            createdAt = it1,
                            updatedAt = it2
                        )
                    }
                }
            }
        }
    }
}
fun Supplier.toDTO(): SupplierDTO = SupplierDTO.fromEntity(this)


class ValidationException(message: String) : Exception(message)