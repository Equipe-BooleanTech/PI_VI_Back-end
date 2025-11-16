package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.FoodResponse
import edu.fatec.petwise.domain.repository.FoodRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetFoodByIdUseCase(
    private val foodRepository: FoodRepository
) {
    fun execute(id: UUID): FoodResponse? {
        val food = foodRepository.findById(id)
        return if (food.isPresent) FoodResponse.fromEntity(food.get()) else null
    }
}
