package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.HygieneResponse
import edu.fatec.petwise.application.dto.HygieneRequest
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.HygieneRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateHygieneUseCase(
    private val hygieneRepository: HygieneRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, request: HygieneRequest, authentication: Authentication): HygieneResponse {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PETSHOP) {
            throw IllegalArgumentException("Apenas usuários PETSHOP podem atualizar produtos de higiene")
        }

        val existingHygiene = hygieneRepository.findById(id).orElseThrow { IllegalArgumentException("Produto de higiene não encontrado") }

        if (existingHygiene.userId != userId) {
            throw IllegalArgumentException("Produto de higiene não pertence ao usuário")
        }

        existingHygiene.name = request.name
        existingHygiene.brand = request.brand
        existingHygiene.category = request.category
        existingHygiene.description = request.description
        existingHygiene.price = request.price
        existingHygiene.stock = request.stock
        existingHygiene.unit = request.unit
        existingHygiene.expiryDate = request.expiryDate
        existingHygiene.imageUrl = request.imageUrl
        existingHygiene.active = request.active
        existingHygiene.updatedAt = LocalDateTime.now()

        val savedHygiene = hygieneRepository.save(existingHygiene)
        return HygieneResponse.fromEntity(savedHygiene)
    }
}
