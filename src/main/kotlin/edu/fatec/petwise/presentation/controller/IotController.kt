package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.application.dto.*
import edu.fatec.petwise.infrastructure.service.IotService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/iot")
class IotController(
    private val iotService: IotService
) {

    @PostMapping("/start-pairing")
    fun startPairing(@RequestBody request: StartPairingRequest): ResponseEntity<Any> {
        return try {
            iotService.startPairingMode(request.petId, request.readerId)
            ResponseEntity.ok(mapOf(
                "message" to "Aproxime a tag do leitor ${request.readerId} agora...",
                "status" to "WAITING_TAG",
                "timeout" to 60
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping("/check-in")
    fun handleCheckIn(@RequestBody request: IotCheckInRequest): ResponseEntity<Any> {
        return try {
            val response = iotService.processTagRead(request)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            val status = if (e.message?.contains("nao cadastrada") == true) 404 else 400
            ResponseEntity.status(status).body(mapOf("error" to e.message))
        }
    }
    
    @PostMapping("/rfid/read")
    fun handleRfidRead(@RequestBody request: RfidTagReadRequest): ResponseEntity<RfidTagRegistrationResponse> {
        return try {
            val response = iotService.processRfidTagRead(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                RfidTagRegistrationResponse(
                    success = false,
                    tagId = null,
                    tagUid = request.tagUid,
                    petId = null,
                    message = e.message ?: "Erro ao processar leitura RFID"
                )
            )
        }
    }

    
    @GetMapping("/pet-by-tag/{tagUid}")
    fun getPetByTag(@PathVariable tagUid: String): ResponseEntity<PetWithOwnerResponse> {
        return try {
            val response = iotService.getPetByTagUid(tagUid)
            ResponseEntity.ok(response)
        } catch (e: RuntimeException) {
            ResponseEntity.status(404).body(null)
        }
    }
    

    @GetMapping("/last-read")
    fun getLastTagRead(): ResponseEntity<Any> {
        val lastRead = iotService.getLastTagRead()
        
        return if (lastRead != null) {
            ResponseEntity.ok(lastRead)
        } else {
            ResponseEntity.ok(mapOf("message" to "Nenhuma tag lida ainda"))
        }
    }
    
    @GetMapping("/pairing/status/{readerId}")
    fun getPairingStatus(@PathVariable readerId: String): ResponseEntity<Map<String, Any>> {
        return try {
            val status = iotService.getPairingStatus(readerId)
            ResponseEntity.ok(mapOf(
                "readerId" to readerId,
                "isPairing" to status.isPairing,
                "petId" to (status.petId?.toString() ?: ""),
                "message" to status.message
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erro desconhecido")))
        }
    }
    
    @DeleteMapping("/pairing/cancel/{readerId}")
    fun cancelPairing(@PathVariable readerId: String): ResponseEntity<Map<String, String>> {
        return try {
            iotService.cancelPairingMode(readerId)
            ResponseEntity.ok(mapOf("message" to "Pareamento cancelado para leitor $readerId"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erro ao cancelar pareamento")))
        }
    }
}