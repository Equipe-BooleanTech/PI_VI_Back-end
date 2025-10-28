package edu.fatec.petwise.application.dto

data class ResetPasswordDto(
    val token: String,
    val newPassword: String
)