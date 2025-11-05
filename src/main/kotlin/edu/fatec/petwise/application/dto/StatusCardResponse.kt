package edu.fatec.petwise.application.dto

data class StatusCardResponse(
    val tipo: String,
    val titulo: String,
    val valor: Int,
    val icone: String,
    val cor: String,
    val descricao: String? = null
)
