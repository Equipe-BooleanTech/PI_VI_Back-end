package edu.fatec.petwise.application.dto

data class AppointmentListResponse(
    val consultas: List<AppointmentResponse>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)