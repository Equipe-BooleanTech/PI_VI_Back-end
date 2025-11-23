package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Toy
import java.util.Optional
import java.util.UUID

interface ToyRepository {
    fun findById(id: UUID): Optional<Toy>
    fun save(toy: Toy): Toy
    fun findByUserId(userId: UUID): List<Toy>
    fun deleteById(id: UUID)
}
