package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ToyRequest
import edu.fatec.petwise.application.dto.ToyResponse
import edu.fatec.petwise.domain.entity.Toy
import edu.fatec.petwise.domain.repository.ToyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class CreateToyUseCase(
    private val toyRepository: ToyRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun execute(userId: UUID, request: ToyRequest): ToyResponse {
        val existingToy = toyRepository.findByUserId(userId)
            .firstOrNull { it.name.equals(request.name, ignoreCase = true) && it.brand.equals(request.brand, ignoreCase = true) }

        if (existingToy != null) {
            throw IllegalArgumentException("Brinquedo já cadastrado com este nome e marca")
        }

        val now = LocalDateTime.now()
        val toy = Toy(
            id = null,
            userId = userId,
            name = request.name,
            brand = request.brand,
            category = request.category,
            description = request.description,
            price = request.price,
            stock = request.stock,
            unit = request.unit,
            material = request.material,
            ageRecommendation = request.ageRecommendation,
            imageUrl = request.imageUrl,
            active = request.active,
            createdAt = now,
            updatedAt = now
        )

        val savedToy = toyRepository.save(toy)

        logger.info("Brinquedo criado: ID=${savedToy.id}, Nome=${savedToy.name}")

        return ToyResponse.fromEntity(savedToy)
    }
}
