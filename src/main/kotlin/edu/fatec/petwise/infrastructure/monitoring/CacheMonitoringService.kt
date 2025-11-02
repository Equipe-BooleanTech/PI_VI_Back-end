package edu.fatec.petwise.infrastructure.monitoring

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class CacheMonitoringService {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var cacheManager: CacheManager

    @Scheduled(fixedRate = 300000)
    fun logCacheStats() {
        logger.info("=== RELATÓRIO DE CACHE ===")

        val cacheNames = cacheManager.cacheNames
        cacheNames.forEach { cacheName ->
            val cache = cacheManager.getCache(cacheName) ?: return@forEach

            val nativeCache = cache.nativeCache as? com.github.benmanes.caffeine.cache.Cache<*, *>
            val stats = nativeCache?.stats()

            if (stats != null) {
                val hitRate = (stats.hitRate() * 100).roundToInt()
                val evictions = stats.evictionCount()

                logger.info("Cache: $cacheName")
                logger.info("  Hits: ${stats.hitCount()}")
                logger.info("  Misses: ${stats.missCount()}")
                logger.info("  Hit Rate: $hitRate%")
                logger.info("  Evictions: $evictions")
                logger.info("  Size: ${nativeCache.estimatedSize()}")
            }
        }
        logger.info("=======================")
    }

    fun clearAllCaches() {
        cacheManager.cacheNames.forEach { cacheName ->
            cacheManager.getCache(cacheName)?.clear()
            logger.info("Cache limpo: $cacheName")
        }
    }

    fun clearCache(cacheName: String) {
        cacheManager.getCache(cacheName)?.clear()
        logger.info("Cache limpo: $cacheName")
    }
}