package edu.fatec.petwise.application.dto

import jakarta.validation.constraints.*

data class ToyRequest(
    @field:NotBlank(message = "Nome é obrigatório")
    @field:Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    val name: String,

    @field:NotBlank(message = "Marca é obrigatória")
    @field:Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    val brand: String,

    @field:NotBlank(message = "Categoria é obrigatória")
    @field:Size(max = 50, message = "Categoria deve ter no máximo 50 caracteres")
    val category: String,

    @field:Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    val description: String? = null,

    @field:NotNull(message = "Preço é obrigatório")
    @field:Positive(message = "Preço deve ser positivo")
    val price: Double,

    @field:NotNull(message = "Estoque é obrigatório")
    @field:Min(value = 0, message = "Estoque deve ser maior ou igual a 0")
    val stock: Int,

    @field:NotBlank(message = "Unidade é obrigatória")
    @field:Size(max = 20, message = "Unidade deve ter no máximo 20 caracteres")
    val unit: String,

    @field:Size(max = 100, message = "Material deve ter no máximo 100 caracteres")
    val material: String? = null,

    @field:Size(max = 50, message = "Recomendação de idade deve ter no máximo 50 caracteres")
    val ageRecommendation: String? = null,

    @field:Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    val imageUrl: String? = null,

    val active: Boolean = true
)
