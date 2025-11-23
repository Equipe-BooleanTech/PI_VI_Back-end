package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.HygieneRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeleteHygieneUseCase(
    private val hygieneRepository: HygieneRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        if (user.userType != UserType.PETSHOP) {
            throw IllegalArgumentException("Apenas usuários PETSHOP podem deletar produtos de higiene")
        }

        val hygiene = hygieneRepository.findById(id).orElseThrow { IllegalArgumentException("Produto de higiene não encontrado") }

        if (hygiene.userId != userId) {
            throw IllegalArgumentException("Produto de higiene não pertence ao usuário")
        }

        hygieneRepository.deleteById(id)
    }
}
