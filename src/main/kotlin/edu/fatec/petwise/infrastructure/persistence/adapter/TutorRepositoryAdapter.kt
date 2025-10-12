package edu.fatec.petwise.infrastructure.persistence.adapter

import edu.fatec.petwise.domain.entity.Tutor
import edu.fatec.petwise.domain.repository.TutorRepository
import edu.fatec.petwise.domain.valueobject.Email
import edu.fatec.petwise.domain.valueobject.Telefone
import edu.fatec.petwise.infrastructure.persistence.entity.TutorEntity
import edu.fatec.petwise.infrastructure.persistence.jpa.JpaTutorRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class TutorRepositoryAdapter(
    private val jpaTutorRepository: JpaTutorRepository
) : TutorRepository {

    override fun save(tutor: Tutor): Tutor {
        val entity = tutor.toEntity()
        val saved = jpaTutorRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: UUID): Tutor? {
        return jpaTutorRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findAll(): List<Tutor> {
        return jpaTutorRepository.findAll().map { it.toDomain() }
    }

    override fun findByCpf(cpf: String): Tutor? {
        return jpaTutorRepository.findByCpf(cpf)?.toDomain()
    }

    override fun findByEmail(email: String): Tutor? {
        return jpaTutorRepository.findByEmail(email)?.toDomain()
    }

    override fun existsById(id: UUID): Boolean {
        return jpaTutorRepository.existsById(id)
    }

    override fun existsByCpf(cpf: String): Boolean {
        return jpaTutorRepository.existsByCpf(cpf)
    }

    override fun existsByEmail(email: String): Boolean {
        return jpaTutorRepository.existsByEmail(email)
    }

    override fun delete(id: UUID) {
        jpaTutorRepository.deleteById(id)
    }

    override fun update(tutor: Tutor): Tutor {
        val entity = tutor.toEntity()
        val saved = jpaTutorRepository.save(entity)
        return saved.toDomain()
    }

    private fun Tutor.toEntity() = TutorEntity(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = this.email.value,
        phone = this.phone.value,
        address = this.address,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )

    private fun TutorEntity.toDomain() = Tutor(
        id = this.id,
        name = this.name,
        cpf = this.cpf,
        email = Email(this.email),
        phone = Telefone(this.phone),
        address = this.address,
        active = this.active,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
