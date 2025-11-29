package edu.fatec.petwise.infrastructure.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MqttCommandService(
    private val mqttGateway: MqttGateway,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(MqttCommandService::class.java)

    fun sendPairingCommand(readerId: String, petId: String) {
        val command = mapOf(
            "command" to "START_PAIRING",
            "petId" to petId,
            "timeout" to 60,
            "timestamp" to LocalDateTime.now().toString()
        )

        val topic = "petwise/rfid/$readerId/command"
        val payload = objectMapper.writeValueAsString(command)

        logger.info("🔗 Enviando comando de pareamento - Reader: $readerId, Pet: $petId")
        mqttGateway.sendMessage(payload, topic)
    }

    fun sendCancelPairingCommand(readerId: String) {
        val command = mapOf(
            "command" to "CANCEL_PAIRING",
            "timestamp" to LocalDateTime.now().toString()
        )

        val topic = "petwise/rfid/$readerId/command"
        val payload = objectMapper.writeValueAsString(command)

        logger.info("❌ Enviando comando de cancelamento - Reader: $readerId")
        mqttGateway.sendMessage(payload, topic)
    }

    fun sendPetInfoResponse(readerId: String, petData: Map<String, Any?>) {
        val response = mapOf(
            "success" to true,
            "data" to petData,
            "timestamp" to LocalDateTime.now().toString()
        )

        val topic = "petwise/rfid/$readerId/pet-info"
        val payload = objectMapper.writeValueAsString(response)

        logger.info("📋 Enviando informações do pet - Reader: $readerId")
        mqttGateway.sendMessage(payload, topic)
    }

    fun requestStatus(readerId: String) {
        val request = mapOf(
            "command" to "GET_STATUS",
            "timestamp" to LocalDateTime.now().toString()
        )

        val topic = "petwise/rfid/$readerId/command"
        val payload = objectMapper.writeValueAsString(request)

        logger.info("📊 Solicitando status - Reader: $readerId")
        mqttGateway.sendMessage(payload, topic)
    }
}
