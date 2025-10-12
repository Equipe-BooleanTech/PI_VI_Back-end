package edu.fatec.petwise.infrastructure.persistence.jpa

import edu.fatec.petwise.infrastructure.persistence.entity.PetEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface JpaPetRepository : JpaRepository<PetEntity, UUID> {
    fun findByTutorId(tutorId: UUID): List<PetEntity>
    fun findByTutorIdAndActive(tutorId: UUID, active: Boolean): List<PetEntity>
}
