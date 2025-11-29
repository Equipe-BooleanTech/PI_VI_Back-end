package edu.fatec.petwise.infrastructure.service

import edu.fatec.petwise.application.dto.*
import edu.fatec.petwise.domain.entity.PetTag
import edu.fatec.petwise.domain.repository.PetRepository
import edu.fatec.petwise.domain.repository.PetTagRepository
import edu.fatec.petwise.infrastructure.persistence.repository.jpa.JpaUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Service
class IotService(
    private val petTagRepository: PetTagRepository,
    private val petRepository: PetRepository,
    private val userRepository: JpaUserRepository
) {

    private val pairingCache = ConcurrentHashMap<String, UUID>()
    private val pairingTimestamps = ConcurrentHashMap<String, LocalDateTime>()
    
    @Volatile
    private var lastTagRead: LastTagReadResponse? = null
    
    companion object {
        private const val PAIRING_TIMEOUT_SECONDS = 60L
    }

    fun startPairingMode(petId: UUID, readerId: String) {
        if (petRepository.findById(petId).isEmpty) {
            throw RuntimeException("Pet não encontrado com ID: $petId")
        }

        cleanExpiredPairings()
        
        pairingCache[readerId] = petId
        pairingTimestamps[readerId] = LocalDateTime.now()
        
        println("IOT: Leitor $readerId agora está esperando uma tag para o Pet $petId")
    }
    
    fun cancelPairingMode(readerId: String) {
        pairingCache.remove(readerId)
        pairingTimestamps.remove(readerId)
        println("IOT: Pareamento cancelado para leitor $readerId")
    }
    
    fun getPairingStatus(readerId: String): PairingStatusResponse {
        cleanExpiredPairings()
        
        val petId = pairingCache[readerId]
        val isPairing = petId != null
        
        return PairingStatusResponse(
            isPairing = isPairing,
            petId = petId,
            message = if (isPairing) "Aguardando leitura de tag" else "Leitor disponível"
        )
    }
    
    private fun cleanExpiredPairings() {
        val now = LocalDateTime.now()
        val expiredReaders = pairingTimestamps.entries
            .filter { (_, timestamp) -> 
                timestamp.plusSeconds(PAIRING_TIMEOUT_SECONDS).isBefore(now) 
            }
            .map { it.key }
        
        expiredReaders.forEach { readerId ->
            pairingCache.remove(readerId)
            pairingTimestamps.remove(readerId)
            println("IOT: Pareamento expirado para leitor $readerId")
        }
    }

    fun processTagRead(request: IotCheckInRequest): IotCheckInResponse {
        cleanExpiredPairings()

        if (pairingCache.containsKey(request.reader_id)) {
            val response = registerNewTag(request.tag_uid, request.reader_id)
            updateLastTagRead(request.tag_uid, request.reader_id, response.petId)
            return response
        }

        val response = performCheckIn(request.tag_uid)
        updateLastTagRead(request.tag_uid, request.reader_id, response.petId)
        return response
    }
    
    fun processRfidTagRead(request: RfidTagReadRequest): RfidTagRegistrationResponse {
        cleanExpiredPairings()
        
        val existingTag = petTagRepository.findByTagUid(request.tagUid)
        
        if (existingTag != null) {
            return RfidTagRegistrationResponse(
                success = false,
                tagId = existingTag.id,
                tagUid = request.tagUid,
                petId = existingTag.petId,
                message = "Tag RFID já cadastrada no sistema",
                timestamp = request.timestamp
            )
        }
        
        val pairingPetId = pairingCache[request.readerId]
        
        if (pairingPetId == null) {
            return RfidTagRegistrationResponse(
                success = false,
                tagId = null,
                tagUid = request.tagUid,
                petId = null,
                message = "Nenhum pet aguardando pareamento neste leitor",
                timestamp = request.timestamp
            )
        }
        
        val newTag = PetTag(
            id = null,
            tagUid = request.tagUid,
            petId = pairingPetId,
            active = true,
            createdAt = request.timestamp,
            updatedAt = request.timestamp
        )
        
        val savedTag = petTagRepository.save(newTag)
        
        pairingCache.remove(request.readerId)
        pairingTimestamps.remove(request.readerId)
        
        println("IOT: Tag RFID ${request.tagUid} registrada com sucesso para Pet $pairingPetId")
        
        return RfidTagRegistrationResponse(
            success = true,
            tagId = savedTag.id,
            tagUid = savedTag.tagUid,
            petId = savedTag.petId,
            message = "Tag RFID cadastrada com sucesso",
            timestamp = request.timestamp
        )
    }
    
    private fun updateLastTagRead(tagUid: String, readerId: String, petId: UUID) {
        try {
            val petData = getPetByTagUid(tagUid)
            lastTagRead = LastTagReadResponse(
                tagUid = tagUid,
                readerId = readerId,
                timestamp = LocalDateTime.now(),
                petFound = true,
                petData = petData
            )
        } catch (e: Exception) {
            lastTagRead = LastTagReadResponse(
                tagUid = tagUid,
                readerId = readerId,
                timestamp = LocalDateTime.now(),
                petFound = false,
                petData = null
            )
        }
    }
    
    // 🆕 Frontend consulta última leitura
    fun getLastTagRead(): LastTagReadResponse? {
        return lastTagRead
    }

    // Lógica de Cadastro (Privada)
    private fun registerNewTag(tagUid: String, readerId: String): IotCheckInResponse {
        val petId = pairingCache[readerId]!! // Pega o ID do Pet da memória

        // Verifica se a tag já não é de outro bicho
        val tagExistente = petTagRepository.findByTagUid(tagUid)
        if (tagExistente != null) {
            // Se já existe, removemos do modo de pareamento para evitar loops de erro
            pairingCache.remove(readerId)
            // Retornamos uma mensagem de erro para aparecer no OLED
            // Usamos um Pet temporário ou null safe para retornar o erro
            return IotCheckInResponse(petId, "ERRO", "TAG JA", "USADA", "", "ERRO: TAG EM USO")
        }

        // Salva a nova tag
        val novaTag = PetTag(
            id = null,
            tagUid = tagUid,
            petId = petId,
            active = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        petTagRepository.save(novaTag)

        // Tira do modo de espera
        pairingCache.remove(readerId)

        return buildResponse(petId, "Cadastrado!")
    }

    // Lógica de Check-in (Privada)
    private fun performCheckIn(tagUid: String): IotCheckInResponse {
        val petTag = petTagRepository.findByTagUid(tagUid)
            ?: throw RuntimeException("Tag nao cadastrada") // Isso vira 404 no Controller

        if (!petTag.active) throw RuntimeException("Tag inativa")

        return buildResponse(petTag.petId, "Bem-vindo")
    }

    // Auxiliar para montar a resposta JSON bonita
    private fun buildResponse(petId: UUID, message: String): IotCheckInResponse {
        val pet = petRepository.findById(petId)
            .orElseThrow { RuntimeException("Pet nao encontrado") }

        val owner = userRepository.findById(pet.ownerId).orElse(null)

        return IotCheckInResponse(
            petId = pet.id!!,
            petName = pet.name,
            ownerName = owner?.fullName ?: "Desconhecido",
            species = pet.species.name,
            ownerPhone = owner?.phone ?: "",
            message = message
        )
    }

    // 🆕 NOVO: Buscar Pet completo por Tag UID (para o frontend exibir informações)
    fun getPetByTagUid(tagUid: String): PetWithOwnerResponse {
        // 1. Buscar a tag
        val petTag = petTagRepository.findByTagUid(tagUid)
            ?: throw RuntimeException("Tag NFC não cadastrada no sistema")

        if (!petTag.active) {
            throw RuntimeException("Tag NFC está inativa")
        }

        // 2. Buscar o Pet
        val pet = petRepository.findById(petTag.petId)
            .orElseThrow { RuntimeException("Pet não encontrado para esta tag") }

        // 3. Buscar o Dono
        val owner = userRepository.findById(pet.ownerId)
            .orElseThrow { RuntimeException("Dono do pet não encontrado") }

        // 4. Montar resposta completa
        return PetWithOwnerResponse(
            // Dados do Pet
            petId = pet.id!!,
            petName = pet.name,
            breed = pet.breed,
            species = pet.species.name,
            gender = pet.gender.name,
            age = pet.age,
            weight = pet.weight,
            healthStatus = pet.healthStatus.name,
            birthDate = pet.birthDate,
            healthHistory = pet.healthHistory,
            profileImageUrl = pet.profileImageUrl,
            isFavorite = pet.isFavorite,
            nextAppointment = pet.nextAppointment,

            // Dados do Dono
            ownerId = owner.id!!,
            ownerName = owner.fullName,
            ownerEmail = owner.email,
            ownerPhone = owner.phone,
            ownerCpf = owner.cpf,
            ownerUserType = owner.userType.name,

            // Metadados
            nfcTagUid = tagUid,
            lastCheckIn = LocalDateTime.now()
        )
    }
}