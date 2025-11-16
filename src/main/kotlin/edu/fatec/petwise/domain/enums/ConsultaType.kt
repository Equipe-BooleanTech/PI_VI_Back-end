package edu.fatec.petwise.domain.enums

enum class ConsultaType(val displayName: String) {
    ROUTINE("Consulta de Rotina"),
    EMERGENCY("Emergência"),
    FOLLOW_UP("Retorno"),
    VACCINATION("Vacinação"),
    SURGERY("Cirurgia"),
    EXAM("Exame"),
    OTHER("Outro")
}
