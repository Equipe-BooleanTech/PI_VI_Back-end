package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.SaleDTO
import edu.fatec.petwise.domain.enums.PaymentMethod
import edu.fatec.petwise.domain.repository.InventoryRepository
import edu.fatec.petwise.domain.repository.SaleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class SalesStatisticsDTO(
    val totalSales: Long,
    val totalRevenue: BigDecimal,
    val averageTicket: BigDecimal,
    val salesByPaymentMethod: Map<PaymentMethod, Long>,
    val topMedications: List<String>,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime
)

@Service
class ManageSalesUseCase(
    private val saleRepository: SaleRepository,
    private val inventoryRepository: InventoryRepository
) {

    @Transactional
    fun createSale(saleDTO: SaleDTO): SaleDTO {
        // Validação dos dados
        saleDTO.validateTotalAmount()
        
        // Converte DTO para entidade
        val sale = saleDTO.toEntity()
        
        // Gera número único da venda se não informado
        if (sale.saleNumber.isBlank()) {
            sale.saleNumber = generateSaleNumber()
        }
        
        // Salva a venda
        val savedSale = saleRepository.save(sale)
        
        // Atualiza estoque se necessário
        updateInventoryStock(savedSale.medicationName, -savedSale.quantity)
        
        return SaleDTO.fromEntity(savedSale)
    }

    @Transactional
    fun updateSale(id: UUID, saleDTO: SaleDTO): SaleDTO {
        // Busca a venda existente
        val existingSale = saleRepository.findById(id)
            .orElseThrow { NoSuchElementException("Venda não encontrada com ID: $id") }
        
        // Valida os novos dados
        saleDTO.validateTotalAmount()
        
        // Reverte o impacto no estoque da venda original
        updateInventoryStock(existingSale.medicationName, existingSale.quantity)
        
        // Atualiza os dados da venda
        val updatedSale = saleDTO.toEntity().copy(id = existingSale.id)
        
        // Salva a venda atualizada
        val savedSale = saleRepository.save(updatedSale)
        
        // Atualiza estoque com os novos dados
        updateInventoryStock(savedSale.medicationName, -savedSale.quantity)
        
        return SaleDTO.fromEntity(savedSale)
    }

    fun getSale(id: UUID): SaleDTO? {
        return saleRepository.findById(id)
            .map { SaleDTO.fromEntity(it) }
            .orElse(null)
    }

    fun getAllSales(pageable: Pageable): Page<SaleDTO> {
        return saleRepository.findAll(pageable)
            .map { SaleDTO.fromEntity(it) }
    }

    fun getSalesByDateRange(startDate: LocalDateTime, endDate: LocalDateTime, pageable: Pageable): Page<SaleDTO> {
        return saleRepository.findBySaleDateBetween(startDate, endDate, pageable)
            .map { SaleDTO.fromEntity(it) }
    }

    fun getSalesByPaymentMethod(paymentMethod: PaymentMethod, pageable: Pageable): Page<SaleDTO> {
        return saleRepository.findByPaymentMethod(paymentMethod, pageable)
            .map { SaleDTO.fromEntity(it) }
    }

    fun searchSalesByMedication(medicationName: String, pageable: Pageable): Page<SaleDTO> {
        return saleRepository.findByMedicationNameContainingIgnoreCase(medicationName, pageable)
            .map { SaleDTO.fromEntity(it) }
    }

    @Transactional(readOnly = true)
    fun getSalesStatistics(startDate: LocalDateTime, endDate: LocalDateTime): SalesStatisticsDTO {
        val sales = saleRepository.findBySaleDateBetween(startDate, endDate)
        
        val totalSales = sales.size.toLong()
        val totalRevenue = sales.sumOf { it.totalAmount }
        val averageTicket = if (totalSales > 0) {
            totalRevenue.divide(BigDecimal(totalSales), 2, BigDecimal.ROUND_HALF_UP)
        } else {
            BigDecimal.ZERO
        }
        
        val salesByPaymentMethod = sales.groupBy { it.paymentMethod }
            .mapValues { it.value.size.toLong() }
        
        val topMedications = sales.groupBy { it.medicationName }
            .mapValues { it.value.size }
            .entries
            .sortedByDescending { it.value }
            .take(10)
            .map { it.key }
        
        return SalesStatisticsDTO(
            totalSales = totalSales,
            totalRevenue = totalRevenue,
            averageTicket = averageTicket,
            salesByPaymentMethod = salesByPaymentMethod,
            topMedications = topMedications,
            periodStart = startDate,
            periodEnd = endDate
        )
    }
    @Transactional
    fun processSaleWithInventoryUpdate(saleDTO: SaleDTO): SaleDTO {
        // Validação dos dados
        saleDTO.validateTotalAmount()
        
        // Verifica disponibilidade de estoque
        if (!checkStockAvailability(saleDTO.medicationName, saleDTO.quantity)) {
            throw IllegalArgumentException(
                "Estoque insuficiente para o medicamento ${saleDTO.medicationName}. " +
                "Quantidade solicitada: ${saleDTO.quantity}"
            )
        }
        
        // Verifica necessidade de receita para medicamentos controlados
        if (saleDTO.isPrescriptionRequired && saleDTO.prescriptionNumber.isNullOrBlank()) {
            throw IllegalArgumentException(
                "Receita médica obrigatória para este medicamento"
            )
        }
        
        // Processa a venda
        return createSale(saleDTO)
    }

    private fun checkStockAvailability(medicationName: String, quantity: Int): Boolean {
        val inventoryItem = inventoryRepository.findByMedicationName(medicationName)
        return inventoryItem.map { it.currentStock >= quantity }.orElse(false)
    }

    private fun updateInventoryStock(medicationName: String, quantityChange: Int) {
        val inventoryItem = inventoryRepository.findByMedicationName(medicationName)
            .orElseThrow { RuntimeException("Medicamento não encontrado no inventário: $medicationName") }
        
        val newStock = inventoryItem.currentStock + quantityChange
        
        if (newStock < 0) {
            throw IllegalArgumentException(
                "Operação resultaria em estoque negativo. Estoque atual: ${inventoryItem.currentStock}, " +
                "tentativa de retirada: ${Math.abs(quantityChange)}"
            )
        }
        
        val updatedItem = inventoryItem.copy(currentStock = newStock)
        inventoryRepository.save(updatedItem)
    }

    private fun generateSaleNumber(): String {
        val now = LocalDateTime.now()
        val uuid = UUID.randomUUID().toString().take(8)
        return "PET-${now.year}${now.monthValue.toString().padStart(2, '0')}${now.dayOfMonth.toString().padStart(2, '0')}-${now.hour.toString().padStart(2, '0')}${now.minute.toString().padStart(2, '0')}${now.second.toString().padStart(2, '0')}-$uuid"
    }
}
