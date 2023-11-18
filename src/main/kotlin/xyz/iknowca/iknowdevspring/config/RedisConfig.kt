package xyz.iknowca.iknowdevspring.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@PropertySource("classpath:/redis.properties")
@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private val redisHost: String? = null

    @Value("\${spring.data.redis.port}")
    private val redisPort: String? = null

    @Value("\${spring.data.redis.password}")
    private val redisPassword: String? = null

    @Bean
    fun RedisConnectionFactory(): RedisConnectionFactory {
        val configuration = RedisStandaloneConfiguration(redisHost!!, redisPort!!.toInt()).apply {
            password = RedisPassword.of(redisPassword)
        }
        return LettuceConnectionFactory(configuration)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>().apply {
            connectionFactory = RedisConnectionFactory()

            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()

        }
        return template
    }
}