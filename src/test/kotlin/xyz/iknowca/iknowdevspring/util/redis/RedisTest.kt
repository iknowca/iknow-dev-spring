package xyz.iknowca.iknowdevspring.util.redis

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import xyz.iknowca.iknowdevspring.util.redis.service.RedisService

@SpringBootTest
class RedisTest(
    @Autowired
    private val redisService: RedisService
): BehaviorSpec() {
    override fun isolationMode(): IsolationMode {
        return IsolationMode.InstancePerTest
    }

    @TestConfiguration
    class RedisTestConfiguration {

    }

    init {
        val key = "test key"
        val value = "test value"
        val nonExistKey = "non exist key"

        Given("Redis에 key와 value를 저장하는 경우") {
            redisService.setKeyValue(key, value, 0)
            When("정상적으로 저장했다면") {
                val resultValue = redisService.getValueByKey(key)
                Then("올바른 value를 리턴한다") {
                    resultValue.shouldBe(value)
                }
            }
            When("적절하지 않은 키로 검색한다면") {
                val resultValue = redisService.getValueByKey(nonExistKey)
                Then("null을 리턴한다") {
                    resultValue.shouldBe(null)
                }
            }
        }
    }
}