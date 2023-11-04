package xyz.iknowca.iknowdevspring.domain.account

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl

@SpringBootTest
class SignInApiTest : BehaviorSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        val accountRepository = mockk<AccountRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(AccountController(AccountServiceImpl(accountRepository))).build()

        val email = "test@gmail.com"
        val password = "password"

        Given("특정 계정이 존재 할 때") {

            every { accountRepository.findByEmailAndPassword(any(), any()) } returns null
            every { accountRepository.findByEmailAndPassword(email, password) } returns Account(
                email,
                password
            ).apply { id = 1L }

            When("사용자가 적합한 이메일과 적합한 패스워드로 로그인을 시도한다면") {

                val signForm = SignForm(email, password)
                val requestBody = jacksonObjectMapper().writeValueAsString(signForm)

                val result = mockMvc.post("/account/sign-in") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    content = requestBody
                }

                Then("상태코드 200ok로 응답한다.") {
                    result.andExpect {
                        status { isOk() }
                    }
                }
                Then("body는 'status':'success', 'accountId':Long 으로 응답한다.") {
                    result.andExpect {
                        jsonPath("$.status") { value("success") }
                        jsonPath("$.accountId") { value("1") }
                    }
                }
            }
            When("사용자가 적합하지 않은 이메일로 로그인을 시도한다면") {
                val wrongEmail = "wrongTest@gmail.com"
                val signForm = SignForm(wrongEmail, password)
                val requestBody = jacksonObjectMapper().writeValueAsString(signForm)

                val result = mockMvc.post("/account/sign-in") {
                    contentType = MediaType.APPLICATION_JSON
                    content = requestBody
                }
                Then("상태코드 401로 응답한다.") {
                    result.andExpect {
                        status { isUnauthorized() }
                    }
                }
            }
            When("사용자가 적합하지 않은 비밀번호로 로그인을 시도한다면") {
                val wrongPassword = "wrongPassword"
                val signForm = SignForm(email, wrongPassword)
                val requestBody = jacksonObjectMapper().writeValueAsString(signForm)

                val result = mockMvc.post("/account/sign-in") {
                    contentType = MediaType.APPLICATION_JSON
                    content = requestBody
                }
                Then("상태코드 401로 응답한다.") {
                    result.andExpect {
                        status { isUnauthorized() }
                    }
                }
            }
            When("사용자가 이메일가 비밀번호 모두 틀렸다면") {
                val wrongEmail = "wrongTest@gmail.com"
                val wrongPassword = "wrongPassword"

                val signForm = SignForm(wrongEmail, wrongPassword)
                val requestBody = jacksonObjectMapper().writeValueAsString(signForm)

                val result = mockMvc.post("/account/sign-in") {
                    contentType = MediaType.APPLICATION_JSON
                    content = requestBody
                }
                Then("상태코드 401로 응답한다.") {
                    result.andExpect {
                        status { isUnauthorized() }
                    }
                }
            }
        }
    }
}