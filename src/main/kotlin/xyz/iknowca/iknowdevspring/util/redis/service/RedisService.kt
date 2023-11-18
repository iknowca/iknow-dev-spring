package xyz.iknowca.iknowdevspring.util.redis.service

interface RedisService {
    fun setKeyValue(key: String, value: String, timeout: Long)
    fun deleteKeyValue(key: String)
    fun getValueByKey(key: String): String?
}