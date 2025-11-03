package edu.fatec.petwise.domain.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "pets")
data class Pet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID = UUID.randomUUID(),

    @Column(name = "owner_id", nullable = false)
    val ownerId: UUID,

    @Column(nullable = false, length = 100)
    var nome: String,

    @Column(nullable = false, length = 50)
    var especie: String,

    @Column(length = 100)
    var raca: String? = null,

    @Column(length = 1)
    var sexo: String? = null,

    @Column(name = "data_nascimento")
    var dataNascimento: LocalDate? = null,

    @Column
    var peso: Double? = null,

    @Column(length = 50)
    var cor: String? = null,

    @Column(columnDefinition = "TEXT")
    var observacoes: String? = null,

    @Column(name = "foto_url", length = 500)
    var fotoUrl: String? = null,

    @Column(nullable = false)
    var ativo: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}
