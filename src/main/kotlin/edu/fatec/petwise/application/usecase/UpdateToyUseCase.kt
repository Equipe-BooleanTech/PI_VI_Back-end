package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ToyResponse
import edu.fatec.petwise.application.dto.ToyRequest
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.ToyRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateToyUseCase(
    private val toyRepository: ToyRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, request: ToyRequest, authentication: Authentication): ToyResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PETSHOP) {
            throw IllegalArgumentException("Apenas usuários PETSHOP podem atualizar brinquedos")
        }

        val existingToy = toyRepository.findById(id).orElseThrow { IllegalArgumentException("Brinquedo não encontrado") }

        if (existingToy.userId != userId) {
            throw IllegalArgumentException("Brinquedo não pertence ao usuário")
        }

        existingToy.name = request.name
        existingToy.brand = request.brand
        existingToy.category = request.category
        existingToy.description = request.description
        existingToy.price = request.price
        existingToy.stock = request.stock
        existingToy.unit = request.unit
        existingToy.material = request.material
        existingToy.ageRecommendation = request.ageRecommendation
        existingToy.imageUrl = request.imageUrl
        existingToy.active = request.active
        existingToy.updatedAt = LocalDateTime.now()

        val savedToy = toyRepository.save(existingToy)
        return ToyResponse.fromEntity(savedToy)
    }
}
