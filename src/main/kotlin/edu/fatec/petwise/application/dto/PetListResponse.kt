package edu.fatec.petwise.application.dto

data class PetListResponse(
    val pets: List<PetResponse>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)
