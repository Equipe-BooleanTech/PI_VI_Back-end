package edu.fatec.petwise.infrastructure.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Primary


@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    @Primary
    fun cacheManager(): CacheManager {
        val caffeineManager = CaffeineCacheManager()
        caffeineManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(15))
                .expireAfterAccess(Duration.ofMinutes(10))
                .recordStats()
                .evictionListener { key, value, cause ->
                    println("Cache evict: $key (motivo: $cause)")
                }
        )

        caffeineManager.isAllowNullValues = false
        return caffeineManager
    }

    @Bean
    fun fastCache(): CacheManager {
        val caffeineManager = CaffeineCacheManager("fastCache")
        caffeineManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(Duration.ofMinutes(5))
                .expireAfterAccess(Duration.ofMinutes(3))
                .recordStats()
        )
        return caffeineManager
    }
}