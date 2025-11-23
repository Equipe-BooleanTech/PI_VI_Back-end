package edu.fatec.petwise.application.usecase
import edu.fatec.petwise.application.dto.FoodResponse
import edu.fatec.petwise.application.dto.FoodRequest
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.FoodRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateFoodUseCase(
    private val foodRepository: FoodRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, request: FoodRequest, authentication: Authentication): FoodResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PETSHOP) {
            throw IllegalArgumentException("Apenas usuários PETSHOP podem atualizar alimentos")
        }

        val existingFood = foodRepository.findById(id).orElseThrow { IllegalArgumentException("Alimento não encontrado") }

        if (existingFood.userId != userId) {
            throw IllegalArgumentException("Alimento não pertence ao usuário")
        }

        existingFood.name = request.name
        existingFood.brand = request.brand
        existingFood.category = request.category
        existingFood.description = request.description
        existingFood.price = request.price
        existingFood.stock = request.stock
        existingFood.unit = request.unit
        existingFood.expiryDate = request.expiryDate
        existingFood.imageUrl = request.imageUrl
        existingFood.active = request.active
        existingFood.updatedAt = LocalDateTime.now()

        val savedFood = foodRepository.save(existingFood)
        return FoodResponse.fromEntity(savedFood)
    }
}
