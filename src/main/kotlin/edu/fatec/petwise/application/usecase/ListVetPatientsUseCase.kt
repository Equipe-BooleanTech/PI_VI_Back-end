package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.PetResponse
import edu.fatec.petwise.domain.entity.Pet
import edu.fatec.petwise.domain.repository.AppointmentRepository
import edu.fatec.petwise.domain.repository.MedicalRecordRepository
import edu.fatec.petwise.domain.repository.PetRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.time.LocalDate

@Service
class ListVetPatientsUseCase(
    private val petRepository: PetRepository,
    private val appointmentRepository: AppointmentRepository,
    private val medicalRecordRepository: MedicalRecordRepository
) {
    fun execute(authentication: Authentication): List<PetResponse> {
        val veterinarian = authentication.name

        // Buscar IDs únicos de pets que têm consultas com este veterinário
        val petIdsWithAppointments = appointmentRepository.findDistinctPetIdsByVeterinarian(veterinarian)

        // Buscar IDs únicos de pets que têm registros médicos com este veterinário
        val petIdsWithMedicalRecords = medicalRecordRepository.findDistinctPetIdsByVeterinarian(veterinarian)

        // Combinar e remover duplicatas
        val allPetIds = (petIdsWithAppointments + petIdsWithMedicalRecords).distinct()

        // Buscar informações dos pets
        val pets = petRepository.findAllById(allPetIds)

        return pets.map { it.toPetResponse() }
    }


// Extension function para Pet entity
private fun Pet.toPetResponse(): PetResponse {
    return PetResponse(
        id = id,
        nome = nome,
        especie = especie,
        raca = raca,
        dataNascimento = dataNascimento,
        sexo = sexo,
        peso = peso,
        cor = cor,
        idade = calculateAge().toString(),
        ativo = ativo,
        createdAt = createdAt,
        updatedAt = updatedAt,
        ownerId = ownerId,
        observacoes = observacoes,
        fotoUrl = fotoUrl
    )
}

// Extension function para calcular idade
private fun Pet.calculateAge(): Int {
    if (dataNascimento == null) return 0

    val currentDate = LocalDate.now()
    val birthLocalDate = dataNascimento

    if (birthLocalDate != null) {
        return currentDate.year - birthLocalDate.year -
                if (currentDate.dayOfYear < birthLocalDate.dayOfYear) 1 else return 0
    }
}