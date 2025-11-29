package edu.fatec.petwise.infrastructure.mqtt

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.MessageHandler

@Configuration
class MqttConfig {

    @Value("\${mqtt.broker.url:tcp://localhost:1883}")
    private lateinit var brokerUrl: String

    @Value("\${mqtt.client.id:petwise-backend}")
    private lateinit var clientId: String

    @Value("\${mqtt.username:}")
    private lateinit var username: String

    @Value("\${mqtt.password:}")
    private lateinit var password: String

    @Bean
    fun mqttClientFactory(): MqttPahoClientFactory {
        val factory = DefaultMqttPahoClientFactory()
        val options = MqttConnectOptions()
        options.serverURIs = arrayOf(brokerUrl)
        options.isCleanSession = true
        options.connectionTimeout = 30
        options.keepAliveInterval = 60
        options.isAutomaticReconnect = true

        if (username.isNotEmpty()) {
            options.userName = username
            options.password = password.toCharArray()
        }

        factory.connectionOptions = options
        return factory
    }

    @Bean
    fun mqttInputChannel(): MessageChannel = DirectChannel()

    @Bean
    fun mqttInbound(): MqttPahoMessageDrivenChannelAdapter {
        val adapter = MqttPahoMessageDrivenChannelAdapter(
            "$clientId-inbound",
            mqttClientFactory(),
            "petwise/rfid/+/read",
            "petwise/rfid/+/checkin",
            "petwise/rfid/+/status"
        )
        adapter.setCompletionTimeout(5000)
        adapter.setConverter(DefaultPahoMessageConverter())
        adapter.setQos(1)
        adapter.outputChannel = mqttInputChannel()
        return adapter
    }

    @Bean
    fun mqttOutboundChannel(): MessageChannel = DirectChannel()

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    fun mqttOutbound(): MessageHandler {
        val messageHandler = MqttPahoMessageHandler("$clientId-outbound", mqttClientFactory())
        messageHandler.setAsync(true)
        messageHandler.setDefaultQos(1)
        messageHandler.setDefaultRetained(false)
        return messageHandler
    }
}
