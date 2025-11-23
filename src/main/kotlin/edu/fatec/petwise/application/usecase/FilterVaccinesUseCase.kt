package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.VaccineResponse
import edu.fatec.petwise.domain.entity.Vaccine
import edu.fatec.petwise.domain.repository.VaccineRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class FilterVaccinesUseCase(
    private val vaccineRepository: VaccineRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(options: Vaccine.VaccineFilterOptions): List<VaccineResponse> {
        var vaccines = vaccineRepository.findAll()

        if (options.petId != null) {
            vaccines = vaccines.filter { it.petId == options.petId }
        }

        if (options.vaccineType != null) {
            vaccines = vaccines.filter { it.vaccineType == options.vaccineType }
        }

        if (options.status != null) {
            vaccines = vaccines.filter { it.status == options.status }
        }

        if (options.startDate != null && options.endDate != null) {
            vaccines = vaccines.filter { vaccine ->
                vaccine.vaccinationDate >= options.startDate && vaccine.vaccinationDate <= options.endDate
            }
        }

        if (options.searchQuery.isNotBlank()) {
            vaccines = vaccines.filter { vaccine ->
                vaccine.observations.contains(options.searchQuery, ignoreCase = true) ||
                vaccine.manufacturer?.contains(options.searchQuery, ignoreCase = true) == true
            }
        }

        logger.info("Filtradas ${vaccines.size} vacinas com opções: $options")

        return vaccines.map { it.toVaccineResponse() }
    }
}
