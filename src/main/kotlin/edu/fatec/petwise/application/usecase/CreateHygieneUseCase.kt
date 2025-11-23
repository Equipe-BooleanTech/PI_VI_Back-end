package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.HygieneRequest
import edu.fatec.petwise.application.dto.HygieneResponse
import edu.fatec.petwise.domain.entity.Hygiene
import edu.fatec.petwise.domain.repository.HygieneRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class CreateHygieneUseCase(
    private val hygieneRepository: HygieneRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun execute(userId: UUID, request: HygieneRequest): HygieneResponse {
        val existingHygiene = hygieneRepository.findByUserId(userId)
            .firstOrNull { it.name.equals(request.name, ignoreCase = true) && it.brand.equals(request.brand, ignoreCase = true) }

        if (existingHygiene != null) {
            throw IllegalArgumentException("Produto de higiene já cadastrado com este nome e marca")
        }

        val now = LocalDateTime.now()
        val hygiene = Hygiene(
            id = null,
            userId = userId,
            name = request.name,
            brand = request.brand,
            category = request.category,
            description = request.description,
            price = request.price,
            stock = request.stock,
            unit = request.unit,
            expiryDate = request.expiryDate,
            imageUrl = request.imageUrl,
            active = request.active,
            createdAt = now,
            updatedAt = now
        )

        val savedHygiene = hygieneRepository.save(hygiene)

        logger.info("Produto de higiene criado: ID=${savedHygiene.id}, Nome=${savedHygiene.name}")

        return HygieneResponse.fromEntity(savedHygiene)
    }
}
