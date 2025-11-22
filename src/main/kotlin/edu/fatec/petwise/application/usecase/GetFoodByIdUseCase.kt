package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.FoodResponse
import edu.fatec.petwise.domain.repository.FoodRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetFoodByIdUseCase(
    private val foodRepository: FoodRepository
) {
    fun execute(userId: UUID, id: UUID): FoodResponse? {
        val food = foodRepository.findById(id)
        return if (food.isPresent && food.get().userId == userId) FoodResponse.fromEntity(food.get()) else null
    }
}
