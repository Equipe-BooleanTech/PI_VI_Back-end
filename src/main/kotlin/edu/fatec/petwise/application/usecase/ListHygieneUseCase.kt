package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.HygieneResponse
import edu.fatec.petwise.domain.repository.HygieneRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ListHygieneUseCase(
    private val hygieneRepository: HygieneRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: UUID, category: String?, searchQuery: String?, activeOnly: Boolean = true): List<HygieneResponse> {
        val hygiene = when {
            category != null && searchQuery != null -> {
                hygieneRepository.findByUserId(userId)
                    .filter { it.category == category && (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            category != null -> {
                hygieneRepository.findByUserId(userId)
                    .filter { it.category == category && (!activeOnly || it.active) }
            }
            searchQuery != null -> {
                hygieneRepository.findByUserId(userId)
                    .filter { (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            else -> {
                hygieneRepository.findByUserId(userId)
                    .filter { !activeOnly || it.active }
            }
        }

        return hygiene.map { HygieneResponse.fromEntity(it) }
    }
}
