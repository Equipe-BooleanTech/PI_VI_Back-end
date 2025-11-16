package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.ToyResponse
import edu.fatec.petwise.domain.repository.ToyRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetToyByIdUseCase(
    private val toyRepository: ToyRepository
) {
    fun execute(id: UUID): ToyResponse? {
        val toy = toyRepository.findById(id)
        return if (toy.isPresent) ToyResponse.fromEntity(toy.get()) else null
    }
}
