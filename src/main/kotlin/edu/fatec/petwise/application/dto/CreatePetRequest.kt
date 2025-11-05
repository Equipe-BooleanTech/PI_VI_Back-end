package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDate

data class CreatePetRequest(
    @field:NotBlank(message = "Nome do pet é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val nome: String,
    
    @field:NotBlank(message = "Espécie é obrigatória")
    @field:Size(max = 50, message = "Espécie deve ter no máximo 50 caracteres")
    val especie: String,
    
    @field:Size(max = 100, message = "Raça deve ter no máximo 100 caracteres")
    val raca: String? = null,
    
    @field:Size(min = 1, max = 1, message = "Sexo deve ser 'M' ou 'F'")
    val sexo: String? = null,
    
    @field:PastOrPresent(message = "Data de nascimento não pode ser futura")
    val dataNascimento: LocalDate? = null,
    
    @field:Positive(message = "Peso deve ser um valor positivo")
    val peso: Double? = null,
    
    @field:Size(max = 50, message = "Cor deve ter no máximo 50 caracteres")
    val cor: String? = null,
    
    @field:Size(max = 1000, message = "Observações devem ter no máximo 1000 caracteres")
    val observacoes: String? = null,
    
    @field:Size(max = 500, message = "URL da foto deve ter no máximo 500 caracteres")
    val fotoUrl: String? = null
)
