package edu.fatec.petwise.application.dto

data class QuickActionResponse(

    val id: String,
    val titulo: String,
    val descricao: String,
    val icone: String,
    val rota: String,
    val cor: String,
    val habilitada: Boolean = true,
    val motivoDesabilitada: String? = null
)
