package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.repository.VaccineRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class DeleteVaccineUseCase(
    private val vaccineRepository: VaccineRepository,
    private val userRepository: UserRepository
) {
    fun execute(id: UUID, authentication: Authentication) {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        
        if (user.userType != UserType.VETERINARY) {
            throw IllegalArgumentException("Apenas veterinários podem excluir vacinas")
        }

        val vaccine = vaccineRepository.findById(id).orElseThrow { IllegalArgumentException("Vacina não encontrada") }

        vaccineRepository.deleteById(id)
    }
}
