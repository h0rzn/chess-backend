package com.github.redis;

import com.github.redis.model.GameWrapper;
import com.github.redis.repository.RedisGameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Profile("dependent-mode")
public class RedisConfiguration {

    @Bean
    public RedisGameRepository gameRepository() {
        return new RedisGameRepository(redisTemplateGameWrapper());
    }

    @Bean
    public RedisTemplate<String, GameWrapper> redisTemplateGameWrapper() {
        RedisTemplate<String, GameWrapper> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(redisStandaloneConfiguration());
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName("localhost");
        redisStandaloneConfiguration.setPort(6379);
        return redisStandaloneConfiguration;
    }

    @Bean
    public <T> RedisGameRepository<T> redisGameRepository(RedisTemplate<String, GameWrapper<T>> redisTemplate) {
        return new RedisGameRepository<>(redisTemplate);
    }
}

