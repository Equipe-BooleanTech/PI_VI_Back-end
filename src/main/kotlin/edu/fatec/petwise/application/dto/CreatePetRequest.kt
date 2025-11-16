package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Positive

data class CreatePetRequest(
    @field:NotBlank(message = "Nome do pet é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val name: String,

    @field:NotBlank(message = "Raça é obrigatória")
    @field:Size(max = 100, message = "Raça deve ter no máximo 100 caracteres")
    val breed: String,

    @field:NotBlank(message = "Espécie é obrigatória")
    @field:Size(max = 50, message = "Espécie deve ter no máximo 50 caracteres")
    val species: String,

    @field:NotBlank(message = "Gênero é obrigatório")
    @field:Size(max = 10, message = "Gênero deve ter no máximo 10 caracteres")
    val gender: String,

    @field:Min(value = 0, message = "Idade deve ser maior ou igual a 0")
    val age: Int,

    @field:Positive(message = "Peso deve ser um valor positivo")
    val weight: Double,

    @field:NotBlank(message = "Status de saúde é obrigatório")
    @field:Size(max = 20, message = "Status de saúde deve ter no máximo 20 caracteres")
    val healthStatus: String,

    @field:NotBlank(message = "Nome do dono é obrigatório")
    @field:Size(max = 100, message = "Nome do dono deve ter no máximo 100 caracteres")
    val ownerName: String,

    @field:NotBlank(message = "Telefone do dono é obrigatório")
    @field:Size(max = 20, message = "Telefone do dono deve ter no máximo 20 caracteres")
    val ownerPhone: String,

    @field:Size(max = 1000, message = "Histórico de saúde deve ter no máximo 1000 caracteres")
    val healthHistory: String = "",

    @field:Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    val profileImageUrl: String? = null
)
