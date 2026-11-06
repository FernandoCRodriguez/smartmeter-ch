package com.energia.consumption;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.resilience.annotation.EnableResilientMethods;

@Configuration
@EnableResilientMethods // activa @Retryable
@EnableCaching          // activa @Cacheable
public class ResilienciaConfig {

    // Caché en memoria, suficiente para un solo servicio. Con varias réplicas
    // o caducidad de entradas se usaría Caffeine o Redis.
    @Bean
    CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("tarifas", "precios");
    }
}