package edu.fatec.petwise.application.dto

import edu.fatec.petwise.domain.entity.Pet
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.UUID

data class PetResponse(
    val id: UUID,
    val ownerId: UUID,
    val nome: String,
    val especie: String,
    val raca: String?,
    val sexo: String?,
    val dataNascimento: LocalDate?,
    val idade: String?, // Calculada: "2 anos e 3 meses"
    val peso: Double?,
    val cor: String?,
    val observacoes: String?,
    val fotoUrl: String?,
    val ativo: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {

        fun fromEntity(pet: Pet): PetResponse {
            return PetResponse(
                id = pet.id,
                ownerId = pet.ownerId,
                nome = pet.nome,
                especie = pet.especie,
                raca = pet.raca,
                sexo = pet.sexo,
                dataNascimento = pet.dataNascimento,
                idade = calcularIdade(pet.dataNascimento),
                peso = pet.peso,
                cor = pet.cor,
                observacoes = pet.observacoes,
                fotoUrl = pet.fotoUrl,
                ativo = pet.ativo,
                createdAt = pet.createdAt,
                updatedAt = pet.updatedAt
            )
        }

        private fun calcularIdade(dataNascimento: LocalDate?): String? {
            if (dataNascimento == null) return null
            
            val period = Period.between(dataNascimento, LocalDate.now())
            val anos = period.years
            val meses = period.months
            
            return when {
                anos > 0 && meses > 0 -> "$anos ${if (anos == 1) "ano" else "anos"} e $meses ${if (meses == 1) "mês" else "meses"}"
                anos > 0 -> "$anos ${if (anos == 1) "ano" else "anos"}"
                meses > 0 -> "$meses ${if (meses == 1) "mês" else "meses"}"
                else -> "Menos de 1 mês"
            }
        }
    }
}
