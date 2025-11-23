package edu.fatec.petwise.domain.enums

enum class OrderStatus(val displayName: String, val description: String) {

    PENDING("Pendente", "Pedido criado e aguardando confirmação do fornecedor"),

    CONFIRMED("Confirmada", "Pedido confirmado e em processamento pelo fornecedor"),

    DELIVERED("Entregue", "Pedido entregue com sucesso"),

    CANCELLED("Cancelada", "Pedido cancelado pelo fornecedor ou cliente");

    override fun toString(): String = displayName
}
