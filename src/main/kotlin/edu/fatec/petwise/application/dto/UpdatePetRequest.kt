package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

data class UpdatePetRequest(
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val name: String? = null,

    @field:Size(max = 100, message = "Raça deve ter no máximo 100 caracteres")
    val breed: String? = null,

    @field:Size(max = 50, message = "Espécie deve ter no máximo 50 caracteres")
    val species: String? = null,

    @field:Size(max = 10, message = "Gênero deve ter no máximo 10 caracteres")
    val gender: String? = null,

    @field:Min(value = 0, message = "Idade deve ser maior ou igual a 0")
    val age: Int? = null,

    @field:Positive(message = "Peso deve ser um valor positivo")
    val weight: Double? = null,

    @field:Size(max = 20, message = "Status de saúde deve ter no máximo 20 caracteres")
    val healthStatus: String? = null,

    @field:Size(max = 100, message = "Nome do dono deve ter no máximo 100 caracteres")
    val ownerName: String? = null,

    @field:Size(max = 20, message = "Telefone do dono deve ter no máximo 20 caracteres")
    val ownerPhone: String? = null,

    @field:Size(max = 1000, message = "Histórico de saúde deve ter no máximo 1000 caracteres")
    val healthHistory: String? = null,

    @field:Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    val profileImageUrl: String? = null,

    val isFavorite: Boolean? = null,

    val nextAppointment: LocalDateTime? = null,

    val active: Boolean? = null
)
