package edu.fatec.petwise.application.usecase

import edu.fatec.petwise.application.dto.CreateTutorRequest
import edu.fatec.petwise.application.dto.TutorResponse
import edu.fatec.petwise.application.dto.UpdateTutorRequest
import edu.fatec.petwise.domain.entity.Tutor
import edu.fatec.petwise.domain.exception.DuplicateEntityException
import edu.fatec.petwise.domain.exception.EntityNotFoundException
import edu.fatec.petwise.domain.repository.TutorRepository
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class CreateTutorUseCase(
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(request: CreateTutorRequest): TutorResponse {
        logger.info("Criando novo tutor com CPF: ${request.cpf}")

        if (tutorRepository.existsByCpf(request.cpf)) {
            logger.warn("Tentativa de criar tutor com CPF duplicado: ${request.cpf}")
            throw DuplicateEntityException("Já existe um tutor cadastrado com este CPF")
        }

        if (tutorRepository.existsByEmail(request.email)) {
            logger.warn("Tentativa de criar tutor com email duplicado: ${request.email}")
            throw DuplicateEntityException("Já existe um tutor cadastrado com este email")
        }

        val tutor = Tutor(
            name = request.name,
            cpf = request.cpf,
            email = Email(request.email),
            phone = Telefone(request.phone),
            address = request.address
        )

        val savedTutor = tutorRepository.save(tutor)
        logger.info("Tutor criado com sucesso. ID: ${savedTutor.id}")
        
        return savedTutor.toResponse()
    }
}

@Service
class GetTutorByIdUseCase(
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID): TutorResponse {
        logger.info("Buscando tutor por ID: $id")
        
        val tutor = tutorRepository.findById(id)
            ?: throw EntityNotFoundException("Tutor", id)
        
        return tutor.toResponse()
    }
}

@Service
class GetAllTutorsUseCase(
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(): List<TutorResponse> {
        logger.info("Buscando todos os tutores")
        return tutorRepository.findAll().map { it.toResponse() }
    }
}

@Service
class UpdateTutorUseCase(
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID, request: UpdateTutorRequest): TutorResponse {
        logger.info("Atualizando tutor ID: $id")
        
        val tutor = tutorRepository.findById(id)
            ?: throw EntityNotFoundException("Tutor", id)

        val updatedTutor = tutor.update(
            name = request.name,
            phone = request.phone?.let { Telefone(it) },
            address = request.address
        )

        val saved = tutorRepository.update(updatedTutor)
        logger.info("Tutor atualizado com sucesso. ID: $id")
        
        return saved.toResponse()
    }
}

@Service
class DeactivateTutorUseCase(
    private val tutorRepository: TutorRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun execute(id: UUID): TutorResponse {
        logger.info("Desativando tutor ID: $id")
        
        val tutor = tutorRepository.findById(id)
            ?: throw EntityNotFoundException("Tutor", id)

        val deactivatedTutor = tutor.deactivate()
        val saved = tutorRepository.update(deactivatedTutor)
        
        logger.info("Tutor desativado com sucesso. ID: $id")
        return saved.toResponse()
    }
}

private fun Tutor.toResponse() = TutorResponse(
    id = this.id.toString(),
    name = this.name,
    cpf = this.cpf,
    email = this.email.value,
    phone = this.phone.value,
    address = this.address,
    active = this.active,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)
