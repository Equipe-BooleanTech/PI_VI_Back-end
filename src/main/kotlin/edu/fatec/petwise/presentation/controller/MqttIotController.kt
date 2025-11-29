package edu.fatec.petwise.presentation.controller

import edu.fatec.petwise.infrastructure.service.IotService
import edu.fatec.petwise.infrastructure.mqtt.MqttCommandService
import edu.fatec.petwise.application.dto.StartPairingRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/iot/mqtt")
class MqttIotController(
    private val iotService: IotService,
    private val mqttCommandService: MqttCommandService
) {

    @PostMapping("/start-pairing")
    fun startPairingMqtt(@RequestBody request: StartPairingRequest): ResponseEntity<Map<String, Any>> {
        return try {
            iotService.startPairingMode(request.petId, request.readerId)
            mqttCommandService.sendPairingCommand(request.readerId, request.petId.toString())
            
            ResponseEntity.ok(mapOf(
                "message" to "Comando de pareamento enviado via MQTT para ${request.readerId}",
                "status" to "WAITING_TAG",
                "timeout" to 60,
                "protocol" to "MQTT"
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erro desconhecido")))
        }
    }

    @DeleteMapping("/pairing/cancel/{readerId}")
    fun cancelPairingMqtt(@PathVariable readerId: String): ResponseEntity<Map<String, String>> {
        return try {
            iotService.cancelPairingMode(readerId)
            mqttCommandService.sendCancelPairingCommand(readerId)
            
            ResponseEntity.ok(mapOf(
                "message" to "Pareamento cancelado via MQTT",
                "readerId" to readerId,
                "protocol" to "MQTT"
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erro ao cancelar")))
        }
    }

    @GetMapping("/pairing/status/{readerId}")
    fun getPairingStatusMqtt(@PathVariable readerId: String): ResponseEntity<Any> {
        return try {
            val status = iotService.getPairingStatus(readerId)
            mqttCommandService.requestStatus(readerId)
            
            ResponseEntity.ok(mapOf(
                "localStatus" to status,
                "mqttStatusRequested" to true,
                "protocol" to "MQTT"
            ))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erro ao consultar status")))
        }
    }

    @GetMapping("/pet-by-tag/{tagUid}")
    fun getPetByTagMqtt(@PathVariable tagUid: String): ResponseEntity<Any> {
        return try {
            val petInfo = iotService.getPetByTagUid(tagUid)
            ResponseEntity.ok(petInfo)
        } catch (e: RuntimeException) {
            val status = if (e.message?.contains("não encontrada") == true) 404 else 400
            ResponseEntity.status(status).body(mapOf("error" to (e.message ?: "Erro ao buscar pet")))
        }
    }
}
