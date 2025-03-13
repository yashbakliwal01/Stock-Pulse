package com.stock.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisConfig {
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
	
	
	//// Converts Java objects to JSON for storing in Redis and back to Java objects when retrieving.
	//serialization used by redis at last point
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
											.entryTtl(Duration.ofMinutes(10))
											.disableCachingNullValues()
											.serializeValuesWith(RedisSerializationContext.SerializationPair
																	.fromSerializer(new GenericJackson2JsonRedisSerializer()));
		//GenericJackson2JsonRedisSerializer is used to handle the conversion.
		return RedisCacheManager.builder().cacheDefaults(config).build();
		
	}
}
