package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.ToyRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteToyUseCase(
    private val toyRepository: ToyRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PETSHOP) {
            throw IllegalArgumentException("Apenas usuários PETSHOP podem deletar brinquedos")
        }

        val toy = toyRepository.findById(id).orElseThrow { IllegalArgumentException("Brinquedo não encontrado") }

        if (toy.userId != userId) {
            throw IllegalArgumentException("Brinquedo não pertence ao usuário")
        }

        toyRepository.deleteById(id)
    }
}
