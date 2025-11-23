package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Hygiene
import java.util.Optional
import java.util.UUID

interface HygieneRepository {
	fun findById(id: UUID): Optional<Hygiene>
	fun save(hygiene: Hygiene): Hygiene
	fun findByUserId(userId: UUID): List<Hygiene>
	fun deleteById(id: UUID)
}
