package edu.fatec.petwise.infrastructure.mqtt

import com.fasterxml.jackson.databind.ObjectMapper
import edu.fatec.petwise.infrastructure.service.IotService
import edu.fatec.petwise.application.dto.IotCheckInRequest
import edu.fatec.petwise.application.dto.RfidTagReadRequest
import org.slf4j.LoggerFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MqttMessageHandler(
    private val iotService: IotService,
    private val mqttGateway: MqttGateway,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(MqttMessageHandler::class.java)

    @ServiceActivator(inputChannel = "mqttInputChannel")
    fun handleMessage(message: Message<*>) {
        try {
            val topic = message.headers["mqtt_receivedTopic"] as String
            val payload = when (val data = message.payload) {
                is String -> data
                is ByteArray -> String(data)
                else -> data.toString()
            }
            
            logger.info("📨 Mensagem MQTT recebida - Topic: $topic")
            logger.debug("Payload: $payload")

            when {
                topic.contains("/read") -> handleRfidRead(topic, payload)
                topic.contains("/checkin") -> handleCheckIn(topic, payload)
                topic.contains("/status") -> handleStatus(topic, payload)
                else -> logger.warn("⚠️  Tópico desconhecido: $topic")
            }

        } catch (e: Exception) {
            logger.error("❌ Erro ao processar mensagem MQTT: ${e.message}", e)
        }
    }

    private fun handleRfidRead(topic: String, payload: String) {
        try {
            val data = objectMapper.readValue(payload, RfidTagReadRequest::class.java)
            val readerId = extractReaderId(topic)

            logger.info("🏷️  RFID Read - Reader: $readerId, Tag: ${data.tagUid}")

            val response = iotService.processRfidTagRead(data)
            val responseTopic = "petwise/rfid/$readerId/response"
            val responsePayload = objectMapper.writeValueAsString(response)

            mqttGateway.sendMessage(responsePayload, responseTopic)
            logger.info("✅ Resposta enviada para: $responseTopic")

        } catch (e: Exception) {
            logger.error("❌ Erro ao processar leitura RFID: ${e.message}", e)
            sendErrorResponse(extractReaderId(topic), e.message ?: "Erro desconhecido")
        }
    }

    private fun handleCheckIn(topic: String, payload: String) {
        try {
            val data = objectMapper.readValue(payload, IotCheckInRequest::class.java)
            val readerId = extractReaderId(topic)

            logger.info("📍 Check-in - Reader: $readerId, Tag: ${data.tag_uid}")

            val response = iotService.processTagRead(data)
            val responseTopic = "petwise/rfid/$readerId/checkin-response"
            val responsePayload = objectMapper.writeValueAsString(response)

            mqttGateway.sendMessage(responsePayload, responseTopic)
            logger.info("✅ Check-in processado para: $responseTopic")

        } catch (e: Exception) {
            logger.error("❌ Erro ao processar check-in: ${e.message}", e)
            sendErrorResponse(extractReaderId(topic), e.message ?: "Erro no check-in")
        }
    }

    private fun handleStatus(topic: String, payload: String) {
        val readerId = extractReaderId(topic)
        logger.info("📊 Status request do reader: $readerId")
        
        try {
            val status = iotService.getPairingStatus(readerId)
            val responseTopic = "petwise/rfid/$readerId/status-response"
            val responsePayload = objectMapper.writeValueAsString(status)
            
            mqttGateway.sendMessage(responsePayload, responseTopic)
            logger.info("✅ Status enviado para: $responseTopic")
            
        } catch (e: Exception) {
            logger.error("❌ Erro ao obter status: ${e.message}", e)
            sendErrorResponse(readerId, e.message ?: "Erro ao consultar status")
        }
    }

    private fun extractReaderId(topic: String): String {
        val parts = topic.split("/")
        return if (parts.size >= 3) parts[2] else "UNKNOWN"
    }

    private fun sendErrorResponse(readerId: String, errorMessage: String) {
        try {
            val errorResponse = mapOf(
                "success" to false,
                "error" to errorMessage,
                "timestamp" to LocalDateTime.now().toString()
            )
            val responseTopic = "petwise/rfid/$readerId/error"
            val payload = objectMapper.writeValueAsString(errorResponse)
            
            mqttGateway.sendMessage(payload, responseTopic)
            logger.info("⚠️  Erro enviado para: $responseTopic")
            
        } catch (e: Exception) {
            logger.error("❌ Falha ao enviar resposta de erro: ${e.message}", e)
        }
    }
}
