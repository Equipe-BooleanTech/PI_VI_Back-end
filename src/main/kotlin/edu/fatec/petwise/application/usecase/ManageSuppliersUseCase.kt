package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.SupplierDTO
import edu.fatec.petwise.application.dto.ValidationResult
import edu.fatec.petwise.application.dto.toDTO
import edu.fatec.petwise.domain.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class ManageSuppliersUseCase(
    private val supplierRepository: SupplierRepository
) {

    fun createSupplier(dto: SupplierDTO): SupplierDTO? {
        require(dto.companyName.isNotBlank()) { "Nome do fornecedor é obrigatório" }

        dto.email?.let {
            require(it.isNotBlank()) { "Email do fornecedor é obrigatório" }
            supplierRepository.findByEmail(it)?.let {
                throw IllegalArgumentException("Já existe fornecedor cadastrado com este email")
            }
        }

        val supplier = dto.toEntity()?.apply {
            createdAt = LocalDateTime.now()
            isActive = true
            rating = 0
        }

        return supplier?.let { supplierRepository.save(it).toDTO() }
    }

    fun updateSupplier(id: UUID, dto: SupplierDTO): SupplierDTO {
        val existingSupplier = supplierRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Fornecedor não encontrado com ID: $id") }

        require(dto.companyName.isNotBlank()) { "Nome do fornecedor é obrigatório" }

        dto.email?.let {
            supplierRepository.findByEmail(it)?.let { other ->
                if (other.id != id) throw IllegalArgumentException("Email já está em uso")
            }
        }

        existingSupplier.apply {
            companyName = dto.companyName
            tradeName = dto.tradeName
            email = dto.email
            phone = dto.phone
            address = dto.address
            city = dto.city
            state = dto.state
            zipCode = dto.zipCode
            updatedAt = LocalDateTime.now()
        }

        return supplierRepository.save(existingSupplier).toDTO()
    }

    fun deleteSupplier(id: UUID): Boolean {
        val supplier = supplierRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Fornecedor não encontrado com ID: $id") }

        supplier.isActive = false
        supplier.updatedAt = LocalDateTime.now()
        supplierRepository.save(supplier)
        return true
    }

    fun getSupplier(id: UUID): SupplierDTO? {
        return supplierRepository.findById(id)
            .filter { it.isActive }
            .map { it.toDTO() }
            .orElse(null)
    }

    fun getAllSuppliers(pageable: Pageable): Page<SupplierDTO> =
        supplierRepository.findAll(pageable).map { it.toDTO() }

    fun getActiveSuppliers(pageable: Pageable): Page<SupplierDTO> =
        supplierRepository.findByIsActiveTrue(pageable).map { it.toDTO() }

    fun searchSuppliers(criteria: String, pageable: Pageable): Page<SupplierDTO> {
        require(criteria.isNotBlank()) { "Critério de busca não pode estar vazio" }
        return supplierRepository.searchSuppliers(criteria, pageable)
            .map { it.toDTO() }
    }

    fun getSuppliersByLocation(state: String, city: String, pageable: Pageable): Page<SupplierDTO> {
        require(state.isNotBlank()) { "Estado é obrigatório" }
        require(city.isNotBlank()) { "Cidade é obrigatória" }

        return supplierRepository.findByStateAndCity(state, city, pageable)
            .map { it.toDTO() }
    }

    fun getSuppliersByRating(minRating: Int, pageable: Pageable): Page<SupplierDTO> {
        require(minRating in 1..5) { "Avaliação deve estar entre 1 e 5" }

        return supplierRepository.findByRatingGreaterThanEqual(minRating, pageable)
            .map { it.toDTO() }
    }

    fun validateSupplierBusiness(supplierId: UUID): ValidationResult {
        val supplier = supplierRepository.findById(supplierId)
            .orElseThrow { IllegalArgumentException("Fornecedor não encontrado com ID: $supplierId") }

        val validations = mutableListOf<ValidationResult.ValidationItem>()
        var isValid = true

        if (supplier.companyName.isBlank()) {
            validations += ValidationResult.ValidationItem("companyName", false, "Nome é obrigatório")
            isValid = false
        }

        if (supplier.email.isNullOrBlank() || !supplier.email!!.contains("@")) {
            validations += ValidationResult.ValidationItem("email", false, "Email inválido")
            isValid = false
        }

        if (supplier.phone.isNullOrBlank()) {
            validations += ValidationResult.ValidationItem("phone", false, "Telefone é obrigatório")
            isValid = false
        }

        if (!supplier.isActive) {
            validations += ValidationResult.ValidationItem("status", false, "Fornecedor inativo")
            isValid = false
        }

        if (supplier.rating!! < 3) {
            validations += ValidationResult.ValidationItem("rating", false, "Avaliação abaixo do mínimo")
            isValid = false
        }

        return ValidationResult(
            supplierId = supplierId,
            isValid = isValid,
            overallStatus = if (isValid) "APROVADO" else "PENDENTE",
            message = if (isValid) "Fornecedor validado com sucesso" else "Fornecedor possui pendências",
            validations = validations,
            validatedAt = LocalDateTime.now()
        )
    }
}
