package edu.fatec.petwise.application.usecase


import edu.fatec.petwise.application.dto.PrescriptionResponse
import edu.fatec.petwise.domain.entity.Prescription
import edu.fatec.petwise.domain.enums.UserType
import edu.fatec.petwise.domain.repository.PrescriptionRepository
import edu.fatec.petwise.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.security.core.Authentication
import java.util.UUID

@Service
class ListPrescriptionsUseCase(
    private val prescriptionRepository: PrescriptionRepository,
    private val userRepository: UserRepository
) {
    fun execute(authentication: Authentication, petId: UUID?, status: String?): List<PrescriptionResponse> {
        val userId = UUID.fromString(authentication.principal.toString())
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("Usuário não encontrado") }

        val prescriptions = when (user.userType) {
            UserType.PHARMACY -> {
                // PHARMACY users can see all prescriptions from VETERINARY users
                when {
                    petId != null && status != null -> {
                        val allPrescriptions = prescriptionRepository.findByPetId(petId)
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    petId != null -> {
                        prescriptionRepository.findByPetId(petId)
                    }
                    status != null -> {
                        val allPrescriptions = prescriptionRepository.findAll()
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    else -> {
                        prescriptionRepository.findAll()
                    }
                }
            }
            UserType.VETERINARY -> {
                // VETERINARY users can see prescriptions they created or for any pet
                when {
                    petId != null && status != null -> {
                        val allPrescriptions = prescriptionRepository.findByPetId(petId)
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    petId != null -> {
                        prescriptionRepository.findByPetId(petId)
                    }
                    status != null -> {
                        val allPrescriptions = prescriptionRepository.findAll()
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    else -> {
                        prescriptionRepository.findAll()
                    }
                }
            }
            UserType.ADMIN -> {
                // ADMIN users can see all prescriptions
                when {
                    petId != null && status != null -> {
                        val allPrescriptions = prescriptionRepository.findByPetId(petId)
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    petId != null -> {
                        prescriptionRepository.findByPetId(petId)
                    }
                    status != null -> {
                        val allPrescriptions = prescriptionRepository.findAll()
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    else -> {
                        prescriptionRepository.findAll()
                    }
                }
            }
            else -> {
                // Other users (OWNER) see prescriptions for their own pets
                when {
                    petId != null && status != null -> {
                        val allPrescriptions = prescriptionRepository.findByPetId(petId)
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    petId != null -> {
                        prescriptionRepository.findByPetId(petId)
                    }
                    status != null -> {
                        val allPrescriptions = prescriptionRepository.findByUserId(userId)
                        val prescriptionStatus = try {
                            Prescription.PrescriptionStatus.valueOf(status.uppercase())
                        } catch (e: IllegalArgumentException) {
                            throw IllegalArgumentException("Status inválido: $status")
                        }
                        allPrescriptions.filter { it.status == prescriptionStatus.name }
                    }
                    else -> {
                        prescriptionRepository.findByUserId(userId)
                    }
                }
            }
        }
        
        return prescriptions.map { PrescriptionResponse.fromEntity(it) }
    }
}
