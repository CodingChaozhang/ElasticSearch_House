package com.lcz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * session会话
 * Created by codingchaozhang.
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 86400)
public class RedisSessionConfig {
	
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {

        return new StringRedisTemplate(factory);
    }
}
