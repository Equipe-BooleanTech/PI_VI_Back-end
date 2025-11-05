package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.InventoryItemDTO
import edu.fatec.petwise.domain.entity.InventoryItem
import edu.fatec.petwise.domain.repository.InventoryRepository
import edu.fatec.petwise.domain.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional
class ManageInventoryUseCase(
    private val inventoryRepository: InventoryRepository,
    private val supplierRepository: SupplierRepository
) {

    fun createInventoryItem(dto: InventoryItemDTO): InventoryItemDTO {
        // Validações de negócio
        validateBusinessRules(dto)
        
        // Verifica se fornecedor existe quando informado
        dto.supplierId?.let { supplierId ->
            validateSupplierExists(supplierId)
        }
        
        // Converte DTO para entidade
        val entity = dto.toEntity()
        
        // Salva no banco
        val savedEntity = inventoryRepository.save(entity)
        
        // Retorna DTO com informações complementares
        return savedEntity.toDTO().enrichWithSupplierInfo()
    }

    fun updateInventoryItem(id: UUID, dto: InventoryItemDTO): InventoryItemDTO {
        // Busca item existente
        val existingItem = inventoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Item do inventário não encontrado com ID: $id") }
        
        // Validações de negócio
        validateBusinessRules(dto)
        
        // Verifica se fornecedor existe quando informado
        dto.supplierId?.let { supplierId ->
            validateSupplierExists(supplierId)
        }
        
        // Atualiza dados
        existingItem.medicationName = dto.medicationName
        existingItem.genericName = dto.genericName
        existingItem.description = dto.description
        existingItem.currentStock = dto.currentStock
        existingItem.minimumStock = dto.minimumStock
        existingItem.maximumStock = dto.maximumStock
        existingItem.unitPrice = dto.unitPrice
        existingItem.supplierId = dto.supplierId
        existingItem.batchNumber = dto.batchNumber
        existingItem.expirationDate = dto.expirationDate
        existingItem.location = dto.location
        existingItem.isActive = dto.isActive
        
        // Salva alterações
        val updatedEntity = inventoryRepository.save(existingItem)
        
        // Retorna DTO atualizado
        return updatedEntity.toDTO().enrichWithSupplierInfo()
    }

    fun deleteInventoryItem(id: UUID): Boolean {
        val existingItem = inventoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Item do inventário não encontrado com ID: $id") }
        
        // Remove lógica: marca como inativo ao invés de deletar fisicamente
        existingItem.isActive = false
        inventoryRepository.save(existingItem)
        
        return true
    }

    fun getInventoryItem(id: UUID): InventoryItemDTO? {
        return inventoryRepository.findById(id)
            .filter { it.isActive }
            .map { it.toDTO().enrichWithSupplierInfo() }
            .orElse(null)
    }

    @Transactional(readOnly = true)
    fun getAllInventoryItems(pageable: Pageable): List<InventoryItemDTO> {
        val page = inventoryRepository.findByIsActiveTrue(pageable)
        return page.content.map { it.toDTO().enrichWithSupplierInfo() }
    }

    @Transactional(readOnly = true)
    fun getLowStockItems(): List<InventoryItemDTO> {
        val items = inventoryRepository.findLowStockItems(Pageable.unpaged())
        return items.content.map { it.toDTO().enrichWithSupplierInfo() }
    }

    @Transactional(readOnly = true)
    fun getExpiringItems(daysAhead: Int): List<InventoryItemDTO> {
        require(daysAhead > 0) { "Número de dias deve ser positivo" }

        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(daysAhead.toLong())

        val itemsPage = inventoryRepository.findExpiringItems(startDate, endDate, Pageable.unpaged())

        return itemsPage.content.map { it.toDTO().enrichWithSupplierInfo() }
    }

    @Transactional(readOnly = true)
    fun searchInventoryItems(name: String, pageable: Pageable): List<InventoryItemDTO> {
        require(name.isNotBlank()) { "Nome do medicamento não pode ser vazio" }
        
        val items = inventoryRepository.findByMedicationNameContainingIgnoreCase(name, pageable)
        return items.content.map { it.toDTO().enrichWithSupplierInfo() }
    }

    fun updateStock(id: UUID, newStock: Int): InventoryItemDTO {
        require(newStock >= 0) { "Estoque não pode ser negativo" }
        
        val existingItem = inventoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Item do inventário não encontrado com ID: $id") }
        
        existingItem.currentStock = newStock
        val updatedEntity = inventoryRepository.save(existingItem)
        
        return updatedEntity.toDTO().enrichWithSupplierInfo()
    }

    private fun validateBusinessRules(dto: InventoryItemDTO) {
        // Valida se estoque mínimo não é maior que máximo
        if (dto.minimumStock > dto.maximumStock) {
            throw BusinessException("Estoque mínimo não pode ser maior que estoque máximo")
        }
        
        // Valida se estoque atual está dentro dos limites
        if (dto.currentStock < 0) {
            throw ValidationException("Estoque atual não pode ser negativo")
        }
        
        if (dto.currentStock > dto.maximumStock) {
            throw ValidationException("Estoque atual não pode ser maior que estoque máximo")
        }
        
        // Valida data de vencimento se informada
        dto.expirationDate?.let { expirationDate ->
            if (expirationDate.isBefore(LocalDate.now())) {
                throw ValidationException("Data de vencimento deve ser futura")
            }
        }
    }

    private fun validateSupplierExists(supplierId: UUID) {
        if (!supplierRepository.existsById(supplierId)) {
            throw BusinessException("Fornecedor não encontrado com ID: $supplierId")
        }
    }

    private fun InventoryItemDTO.toEntity(): InventoryItem {
        return InventoryItem(
            id = this.id,
            medicationName = this.medicationName,
            genericName = this.genericName,
            description = this.description,
            currentStock = this.currentStock,
            minimumStock = this.minimumStock,
            maximumStock = this.maximumStock,
            unitPrice = this.unitPrice,
            supplierId = this.supplierId,
            batchNumber = this.batchNumber,
            expirationDate = this.expirationDate,
            location = this.location,
            isActive = this.isActive
        )
    }

    private fun InventoryItem.toDTO(): InventoryItemDTO {
        return InventoryItemDTO(
            id = this.id,
            medicationName = this.medicationName,
            genericName = this.genericName,
            description = this.description,
            currentStock = this.currentStock,
            minimumStock = this.minimumStock,
            maximumStock = this.maximumStock,
            unitPrice = this.unitPrice,
            supplierId = this.supplierId,
            batchNumber = this.batchNumber,
            expirationDate = this.expirationDate,
            location = this.location,
            isActive = this.isActive,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt
        )
    }

    private fun InventoryItemDTO.enrichWithSupplierInfo(): InventoryItemDTO {
        var enrichedDTO = this
        
        // Adiciona nome do fornecedor se ID informado
        this.supplierId?.let { supplierId ->
            supplierRepository.findById(supplierId).ifPresent { supplier ->
                enrichedDTO = enrichedDTO.copy(supplierName = supplier.companyName)
            }
        }
        
        // Calcula dias até vencimento se data informada
        this.expirationDate?.let { expirationDate ->
            val daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate)
            enrichedDTO = enrichedDTO.copy(
                daysUntilExpiration = daysUntilExpiration.toInt()
            )
        }
        
        // Determina status do estoque
        val isLowStock = this.currentStock <= this.minimumStock
        val stockStatus = when {
            this.currentStock == 0 -> "Esgotado"
            isLowStock -> "Estoque Baixo"
            this.currentStock >= this.maximumStock -> "Estoque Alto"
            else -> "Normal"
        }
        
        return enrichedDTO.copy(
            isLowStock = isLowStock,
            stockStatus = stockStatus
        )
    }
}

class EntityNotFoundException(message: String) : RuntimeException(message)

class ValidationException(message: String) : RuntimeException(message)

class BusinessException(message: String) : RuntimeException(message)
