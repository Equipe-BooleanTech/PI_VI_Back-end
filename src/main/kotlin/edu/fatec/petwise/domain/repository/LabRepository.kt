package edu.fatec.petwise.domain.repository

import edu.fatec.petwise.domain.entity.Lab
import java.util.Optional
import java.util.UUID

interface LabRepository {
	fun findAll(): List<Lab>
	fun finByVeterinaryId(veterinaryId: UUID): List<Lab>
	fun findById(id: UUID): Optional<Lab>
	fun save(lab: Lab): Lab
	fun deleteById(id: UUID)
}
