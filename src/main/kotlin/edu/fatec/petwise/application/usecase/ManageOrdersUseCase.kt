package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.PharmacyOrderDTO
import edu.fatec.petwise.domain.enums.PharmacyOrderStatus
import edu.fatec.petwise.domain.repository.PharmacyOrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class ManageOrdersUseCase(
    private val orderRepository: PharmacyOrderRepository
) {

    @Transactional(readOnly = true)
    fun getAllOrders(pageable: Pageable): Page<PharmacyOrderDTO> =
        orderRepository.findAll(pageable).map { PharmacyOrderDTO.fromEntity(it) }

    @Transactional(readOnly = true)
    fun getOrder(id: UUID): PharmacyOrderDTO? =
        orderRepository.findById(id)
            .map { PharmacyOrderDTO.fromEntity(it) }
            .orElse(null)

    @Transactional
    fun createOrder(dto: PharmacyOrderDTO): PharmacyOrderDTO {
        val entity = PharmacyOrderDTO.toEntity(dto).copy(
            status = PharmacyOrderStatus.PENDING,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val saved = orderRepository.save(entity)
        return PharmacyOrderDTO.fromEntity(saved)
    }

    @Transactional
    fun updateOrderStatus(id: UUID, status: PharmacyOrderStatus): PharmacyOrderDTO {
        val order = orderRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Pedido não encontrado com ID: $id") }

        order.status = status
        order.updatedAt = LocalDateTime.now()

        val updated = orderRepository.save(order)
        return PharmacyOrderDTO.fromEntity(updated)
    }

    @Transactional
    fun deleteOrder(id: UUID): Boolean {
        val order = orderRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Pedido não encontrado com ID: $id") }

        // Como não existe campo "isActive", apenas remove diretamente
        orderRepository.delete(order)
        return true
    }
}
