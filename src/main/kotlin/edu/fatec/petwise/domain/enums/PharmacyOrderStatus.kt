package edu.fatec.petwise.domain.enums

enum class PharmacyOrderStatus(val displayName: String, val description: String) {

    PENDING("Pendente", "Pedido criado e aguardando confirmação do fornecedor"),
    CONFIRMED("Confirmado", "Pedido confirmado e em processamento pelo fornecedor"),
    DELIVERED("Entregue", "Pedido entregue com sucesso"),
    CANCELLED("Cancelado", "Pedido cancelado pelo fornecedor ou cliente");

    override fun toString(): String = displayName
}
