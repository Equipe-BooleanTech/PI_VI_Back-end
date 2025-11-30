package edu.fatec.petwise.infrastructure.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitFilter : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(javaClass)

    
    private val cache = ConcurrentHashMap<String, Bucket>()

    
    private val rateLimits = mapOf(
        "/api/auth/login" to RateLimitConfig(capacity = 5, refillTokens = 5, refillDuration = Duration.ofMinutes(1)),
        "/api/auth/register" to RateLimitConfig(capacity = 3, refillTokens = 3, refillDuration = Duration.ofMinutes(1)),
        "/api/auth/refresh-token" to RateLimitConfig(capacity = 10, refillTokens = 10, refillDuration = Duration.ofMinutes(1))
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI
        val rateLimitConfig = rateLimits[path]

        
        if (rateLimitConfig == null) {
            filterChain.doFilter(request, response)
            return
        }

        val clientIp = getClientIp(request)
        val bucketKey = "$path:$clientIp"

        val bucket = cache.computeIfAbsent(bucketKey) {
            createBucket(rateLimitConfig)
        }

        val probe = bucket.tryConsumeAndReturnRemaining(1)

        if (probe.isConsumed) {
            
            response.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            filterChain.doFilter(request, response)
        } else {
            
            logger.warn("Rate limit excedido para IP $clientIp na rota $path")
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = "application/json"
            response.characterEncoding = "UTF-8"

            val waitTime = probe.nanosToWaitForRefill / 1_000_000_000
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", waitTime.toString())

            response.writer.write("""
                {
                    "error": "too_many_requests",
                    "message": "Muitas tentativas. Tente novamente em $waitTime segundos.",
                    "retryAfter": $waitTime
                }
            """.trimIndent())
        }
    }

    private fun createBucket(config: RateLimitConfig): Bucket {
        val bandwidth = Bandwidth.classic(
            config.capacity,
            Refill.intervally(config.refillTokens, config.refillDuration)
        )
        return Bucket.builder()
            .addLimit(bandwidth)
            .build()
    }

    private fun getClientIp(request: HttpServletRequest): String {
        
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrEmpty()) {
            return xForwardedFor.split(",")[0].trim()
        }

        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrEmpty()) {
            return xRealIp
        }

        return request.remoteAddr
    }

    private data class RateLimitConfig(
        val capacity: Long,
        val refillTokens: Long,
        val refillDuration: Duration
    )
}

@Configuration
class RateLimitConfiguration {
    
    
}
