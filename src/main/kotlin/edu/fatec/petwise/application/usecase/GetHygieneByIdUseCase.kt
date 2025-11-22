package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.HygieneResponse
import edu.fatec.petwise.domain.repository.HygieneRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetHygieneByIdUseCase(
    private val hygieneRepository: HygieneRepository
) {
    fun execute(userId: UUID, id: UUID): HygieneResponse? {
        val hygiene = hygieneRepository.findById(id)
        return if (hygiene.isPresent && hygiene.get().userId == userId) HygieneResponse.fromEntity(hygiene.get()) else null
    }
}
