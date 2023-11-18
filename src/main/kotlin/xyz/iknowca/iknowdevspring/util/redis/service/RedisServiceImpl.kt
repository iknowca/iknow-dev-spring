package xyz.iknowca.iknowdevspring.util.redis.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class RedisServiceImpl(
    val redisTemplate: StringRedisTemplate
): RedisService {
    override fun setKeyValue(key: String, value: String, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS)
    }

    override fun getValueByKey(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    override fun deleteKeyValue(key: String) {
        redisTemplate.delete(key)
    }
}