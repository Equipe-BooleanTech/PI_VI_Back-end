package edu.fatec.petwise.domain.enums

enum class HealthStatus(val description: String) {
    SAUDAVEL("Saudável"),
    DOENTE("Doente"),
    EM_TRATAMENTO("Em Tratamento"),
    RECUPERACAO("Em Recuperação"),
    CRITICO("Crítico"),
    VACINACAO_EM_DIA("Vacinação em Dia"),
    VACINACAO_ATRASADA("Vacinação Atrasada")
}