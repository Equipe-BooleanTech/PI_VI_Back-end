package edu.fatec.petwise.infrastructure.config

import edu.fatec.petwise.infrastructure.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOriginPatterns = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/register",
                        "/api/auth/login",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/api/iot/**",
                        ).permitAll()
                    
                    .requestMatchers("/api/auth/logout").authenticated()
                    .requestMatchers("/api/auth/profile").hasAnyRole(
                        "OWNER", "VETERINARY", "PETSHOP", "PHARMACY"
                    )
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/pets/**"
                    ).hasAnyRole(
                        "OWNER", "VETERINARY", "PETSHOP", "PHARMACY"
                    )
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/pets/**"
                    ).hasAnyRole(
                        "OWNER"
                    )
                    .requestMatchers(
                        HttpMethod.PUT,
                        "/api/pets/**"
                    ).hasAnyRole(
                        "OWNER", "ADMIN"
                    )
                    .requestMatchers(
                        HttpMethod.DELETE,
                        "/api/pets/**"
                    ).hasAnyRole(
                        "OWNER",
                    )
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/appointments/pet/**"
                    ).hasAnyRole(
                        "OWNER"
                    )
                    .requestMatchers(
                        "/api/appointments/**"
                    ).hasAnyRole(
                        "VETERINARY"
                    )
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/medications/pet/**"
                    ).hasAnyRole(
                        "OWNER"
                    )
                    .requestMatchers(
                        "/api/medications/**"
                    ).hasAnyRole(
                        "PHARMACY"
                    )
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/vaccines/pet/**"
                    ).hasAnyRole(
                        "OWNER"
                    )
                    .requestMatchers(
                        "/api/vaccines/**"
                    ).hasAnyRole(
                        "VETERINARY"
                    )
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/exams/pet/**"
                    ).hasAnyRole(
                        "OWNER", "VETERINARY"
                    )
                    .requestMatchers(
                        "/api/exams/**"
                    ).hasAnyRole(
                        "VETERINARY"
                    ).requestMatchers(
                        HttpMethod.GET,
                        "/api/prescriptions/pet/**"
                    ).hasAnyRole(
                        "OWNER", "VETERINARY"
                    )
                    .requestMatchers(
                        "/api/prescriptions/**"
                    ).hasAnyRole(
                        "VETERINARY"
                    )
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/medications/pet/**"
                    ).hasAnyRole(
                        "OWNER", "PHARMACY"
                    ).requestMatchers(
                        "api/medications/**"
                    ).hasAnyRole(
                        "PHARMACY"
                    )
                    .requestMatchers(
                        "/api/foods/**"
                    ).hasAnyRole(
                       "PETSHOP"
                    )
                    .requestMatchers(
                        "/api/hygiene/**"
                    ).hasAnyRole(
                        "PETSHOP"
                    )
                    .requestMatchers(
                        "/api/toys/**"
                    ).hasAnyRole(
                        "PETSHOP"
                    )
                    .requestMatchers(
                        "/api/labs/**"
                    ).hasAnyRole(
                        "VETERINARY"
                    )
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
