package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.FoodResponse
import edu.fatec.petwise.domain.repository.FoodRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class ListFoodsUseCase(
    private val foodRepository: FoodRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(authentication: Authentication, category: String?, searchQuery: String?, activeOnly: Boolean = true): List<FoodResponse> {
        val foods = when {
            category != null && searchQuery != null -> {
                foodRepository.findAll()
                    .filter { it.category == category && (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            category != null -> {
                foodRepository.findAll()
                    .filter { it.category == category && (!activeOnly || it.active) }
            }
            searchQuery != null -> {
                foodRepository.findAll()
                    .filter { (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            else -> {
                foodRepository.findAll()
                    .filter { !activeOnly || it.active }
            }
        }

        return foods.map { FoodResponse.fromEntity(it) }
    }
}
