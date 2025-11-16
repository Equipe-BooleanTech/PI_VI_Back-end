package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.HygieneResponse
import edu.fatec.petwise.domain.repository.HygieneRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class ListHygieneUseCase(
    private val hygieneRepository: HygieneRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(authentication: Authentication, category: String?, searchQuery: String?, activeOnly: Boolean = true): List<HygieneResponse> {
        val hygiene = when {
            category != null && searchQuery != null -> {
                hygieneRepository.findAll()
                    .filter { it.category == category && (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            category != null -> {
                hygieneRepository.findAll()
                    .filter { it.category == category && (!activeOnly || it.active) }
            }
            searchQuery != null -> {
                hygieneRepository.findAll()
                    .filter { (!activeOnly || it.active) }
                    .filter { it.name.contains(searchQuery, ignoreCase = true) || it.brand.contains(searchQuery, ignoreCase = true) }
            }
            else -> {
                hygieneRepository.findAll()
                    .filter { !activeOnly || it.active }
            }
        }

        return hygiene.map { HygieneResponse.fromEntity(it) }
    }
}
