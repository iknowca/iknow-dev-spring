package xyz.iknowca.iknowdevspring.domain.account

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import java.util.*

@SpringBootTest
class AccountTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        val accountRepository = mockk<AccountRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(AccountController(AccountServiceImpl(accountRepository))).build()

        val email = "email@gmail.com"
        val password = "password"
        var id = 1L

        Given("회원 정보를 요청받았을때") {

            every { accountRepository.findById(any()) } returns Optional.empty()
            every { accountRepository.findById(id) } returns Optional.of(Account(email, password).apply { id = 1L })

            When("인증정보가 존재한다면") {
                val result = mockMvc.get("/account") {
                    header("authorization", "1")
                }
                Then("상태코드는 OK")
                    result.andExpect {
                        status { isOk() }
                    }
                Then("email:email@gmail.com") {
                    result.andExpect {
                        jsonPath("$.email"){value("email@gmail.com")}
                    }
                }
            }
            When("인증정보가 존재하지 않는다면") {
                val result = mockMvc.get("/account") {
                    header("authorization", "2")
                }
                Then("상태코드는 401 UNAUTHORIZED") {
                    result.andExpect {
                        status { isUnauthorized() }
                    }
                }
            }
        }
    }
}