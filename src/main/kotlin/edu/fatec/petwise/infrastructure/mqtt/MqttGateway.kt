package edu.fatec.petwise.infrastructure.mqtt

import org.springframework.integration.annotation.MessagingGateway
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
interface MqttGateway {
    fun sendMessage(payload: String, @Header("mqtt_topic") topic: String)
}
