package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.UpdateProfileDto
import edu.fatec.petwise.application.dto.UserResponse
import edu.fatec.petwise.domain.entity.UserType
import edu.fatec.petwise.domain.exception.BusinessRuleException
import edu.fatec.petwise.domain.exception.DuplicateEntityException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.UserRepository
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(userId: String, request: UpdateProfileDto): UserResponse {
        logger.info("Atualizando perfil do usuário: $userId")

        val user = userRepository.findById(UUID.fromString(userId))
            ?: throw EntityNotFoundException("Usuário", userId)

        if (!user.active) {
            throw BusinessRuleException("Usuário inativo não pode atualizar perfil")
        }

        request.fullName?.let { newName ->
            if (newName.isBlank()) {
                throw BusinessRuleException("Nome não pode ser vazio")
            }
            user.fullName = newName.trim()
        }

        request.email?.let { newEmail ->
            if (newEmail != user.email.value) {
                if (userRepository.existsByEmail(newEmail)) {
                    logger.warn("Tentativa de atualizar para email duplicado: ${maskEmail(newEmail)}")
                    throw DuplicateEntityException("Já existe um usuário cadastrado com este email")
                }
                user.email = Email(newEmail)
                logger.info("Email atualizado para usuário: $userId")
            }
        }


        request.phone?.let { newPhone ->
            user.phone = Telefone(newPhone)
        }

        when (user.userType) {
            UserType.OWNER -> {
                request.cpf?.let { newCpf ->
                    val cleanCpf = newCpf.replace(Regex("[^0-9]"), "")
                    if (cleanCpf != user.cpf) {
                        if (userRepository.existsByCpf(cleanCpf)) {
                            throw DuplicateEntityException("Já existe um tutor cadastrado com este CPF")
                        }
                        user.cpf = cleanCpf
                        logger.info("CPF atualizado para usuário: $userId")
                    }
                }
            }

            UserType.VETERINARY -> {
                request.crmv?.let { newCrmv ->
                    if (newCrmv != user.crmv) {
                        if (userRepository.existsByCrmv(newCrmv)) {
                            throw DuplicateEntityException("Já existe um veterinário cadastrado com este CRMV")
                        }
                        user.crmv = newCrmv
                        logger.info("CRMV atualizado para usuário: $userId")
                    }
                }

                request.specialization?.let { newSpec ->
                    if (newSpec.isBlank()) {
                        throw BusinessRuleException("Especialização não pode ser vazia")
                    }
                    user.specialization = newSpec.trim()
                }
            }

            UserType.PHARMACY -> {
                request.cnpj?.let { newCnpj ->
                    val cleanCnpj = newCnpj.replace(Regex("[^0-9]"), "")
                    if (cleanCnpj != user.cnpj) {
                        if (userRepository.existsByCnpj(cleanCnpj)) {
                            throw DuplicateEntityException("Já existe uma farmácia cadastrada com este CNPJ")
                        }
                        user.cnpj = cleanCnpj
                        logger.info("CNPJ atualizado para usuário: $userId")
                    }
                }

                request.companyName?.let { newCompanyName ->
                    if (newCompanyName.isBlank()) {
                        throw BusinessRuleException("Nome da empresa não pode ser vazio")
                    }
                    user.companyName = newCompanyName.trim()
                }
            }

            UserType.ADMIN -> {
                if (request.cpf != null || request.crmv != null ||
                    request.cnpj != null || request.companyName != null || 
                    request.specialization != null) {
                    throw BusinessRuleException("Admin não pode atualizar campos específicos de outros tipos")
                }
            }
        }

        val updatedUser = userRepository.save(user)
        logger.info("Perfil atualizado com sucesso para usuário: $userId")

        return updatedUser.toResponse()
    }

    private fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return "***@***"
        
        val localPart = parts[0]
        val domain = parts[1]
        
        val maskedLocal = if (localPart.length <= 2) {
            "***"
        } else {
            localPart.take(2) + "***"
        }
        
        return "$maskedLocal@$domain"
    }
}

