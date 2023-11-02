package xyz.iknowca.iknowdevspring.domain.account

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.json.JSONObject
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl

@ExtendWith(MockKExtension::class)
class SignInApiTest : BehaviorSpec({

    lateinit var mockMvc: MockMvc

    @MockK
    lateinit var accountRepository: AccountRepository

    beforeContainer {
        accountRepository = mockk<AccountRepository>()
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController(AccountServiceImpl(accountRepository))).build()
    }



    Given("특정 계정이 존재 할 때") {
        val email = "test@gmail.com"
        val password = "password"


        When("사용자가 적합한 이메일과 적합한 패스워드로 로그인을 시도한다면") {

            val signForm = SignForm(email, password)
            val requestBody = jacksonObjectMapper().writeValueAsString(signForm)
            every { accountRepository.findByEmailAndPassword(email, password) } returns Account(email, password).apply { id=1L }

            val result = mockMvc
                .perform(
                    MockMvcRequestBuilders.post("/account/sign-in").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

            Then("상태코드 200ok로 응답한다.") {
                result.response.status shouldBe HttpStatus.OK.value()
            }
            Then("body는 'status':'success', 'accountId':Long 으로 응답한다.") {
                val responseMap = JSONObject(result.response.contentAsString)
                responseMap["status"] shouldBe "success"
                responseMap["accountId"] shouldBe "1"
            }
        }
        When("사용자가 적합하지 않은 이메일로 로그인을 시도한다면") {
            val wrongEmail = "wrongTest@gmail.com"
            val signForm = SignForm(wrongEmail, password)
            val requestBody = jacksonObjectMapper().writeValueAsString(signForm)
            every { accountRepository.findByEmailAndPassword(wrongEmail, password) } returns null

            val result = mockMvc
                .perform(
                    MockMvcRequestBuilders.post("/account/sign-in").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()
            Then("상태코드 401로 응답한다.") {
                result.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }
        When("사용자가 적합하지 않은 비밀번호로 로그인을 시도한다면") {
            val wrongPassword = "wrongPassword"
            val signForm = SignForm(email, wrongPassword)
            val requestBody = jacksonObjectMapper().writeValueAsString(signForm)
            every { accountRepository.findByEmailAndPassword(email, wrongPassword) } returns null

            val result = mockMvc
                .perform(
                    MockMvcRequestBuilders.post("/account/sign-in").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()
            Then("상태코드 401로 응답한다."){
                result.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }
        When("사용자가 이메일가 비밀번호 모두 틀렸다면") {
            val wrongEmail = "wrongTest@gmail.com"
            val wrongPassword = "wrongPassword"

            val signForm = SignForm(wrongEmail, wrongPassword)
            val requestBody = jacksonObjectMapper().writeValueAsString(signForm)
            every { accountRepository.findByEmailAndPassword(wrongEmail, wrongPassword) } returns null

            val result = mockMvc
                .perform(
                    MockMvcRequestBuilders.post("/account/sign-in").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()
            Then("상태코드 401로 응답한다.") {
                result.response.status shouldBe HttpStatus.UNAUTHORIZED.value()
            }
        }
    }

})