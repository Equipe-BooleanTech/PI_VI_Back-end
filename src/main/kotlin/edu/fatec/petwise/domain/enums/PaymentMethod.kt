package edu.fatec.petwise.domain.enums

enum class PaymentMethod(val displayName: String) {
    CASH("Dinheiro"),
    CARD("Cartão"),
    PIX("PIX"),
    CREDIT("Crédito")
}