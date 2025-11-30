package edu.fatec.petwise.infrastructure.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("*")
            .maxAge(3600)
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        val builder = Jackson2ObjectMapperBuilder()
            .modules(JavaTimeModule(), SimpleModule().addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer()))
        converters.add(MappingJackson2HttpMessageConverter(builder.build()))
    }
}

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    private val formatters = listOf(
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
    )

    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDateTime {
        val value = parser.text

        for (formatter in formatters) {
            try {
                return if (formatter == DateTimeFormatter.ofPattern("dd/MM/yyyy")) {
                    
                    val date = java.time.LocalDate.parse(value, formatter)
                    LocalDateTime.of(date, java.time.LocalTime.MIDNIGHT)
                } else {
                    LocalDateTime.parse(value, formatter)
                }
            } catch (e: DateTimeParseException) {
                
            }
        }

        throw DateTimeParseException("Unable to parse LocalDateTime: $value", value, 0)
    }
}
