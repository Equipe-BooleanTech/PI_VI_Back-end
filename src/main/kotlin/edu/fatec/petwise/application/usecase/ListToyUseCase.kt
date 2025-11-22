package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ToyResponse
import edu.fatec.petwise.domain.repository.ToyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListToyUseCase(
    private val toyRepository: ToyRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, category: String?, searchQuery: String?, activeOnly: Boolean = true): List<ToyResponse> {
        val toys = when {
            category != null && searchQuery != null -> {
                toyRepository.findByUserId(userId)
                    .filter { it.category == category && (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            category != null -> {
                toyRepository.findByUserId(userId)
                    .filter { it.category == category && (!activeOnly || it.active) }
            }
            searchQuery != null -> {
                toyRepository.findByUserId(userId)
                    .filter { (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            else -> {
                toyRepository.findByUserId(userId)
                    .filter { !activeOnly || it.active }
            }
        }

        return toys.map { ToyResponse.fromEntity(it) }
    }
}
