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

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/pets/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/pets/**").hasAnyRole("OWNER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/pets/**").hasAnyRole("OWNER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/pets/**").hasAnyRole("OWNER", "ADMIN")
                    .requestMatchers("/api/tutors/**").hasAnyRole("OWNER", "ADMIN", "VETERINARY")
                    .requestMatchers("/api/appointments/**").hasAnyRole("OWNER", "ADMIN", "VETERINARY")
                    .requestMatchers("/api/medications/**").hasAnyRole("OWNER", "ADMIN", "VETERINARY")
                    .requestMatchers("/api/vaccines/**").hasAnyRole("OWNER", "ADMIN", "VETERINARY")
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
