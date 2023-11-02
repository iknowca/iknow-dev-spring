package xyz.iknowca.iknowdevspring.domain.account

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.json.JSONObject
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl

@ExtendWith(MockKExtension::class)
class SignUpApiTest : BehaviorSpec({

    lateinit var mockMvc: MockMvc

    @MockK
    lateinit var accountRepository: AccountRepository

    beforeSpec {
        accountRepository = mockk<AccountRepository>()
        mockMvc = MockMvcBuilders.standaloneSetup(AccountController(AccountServiceImpl(accountRepository))).build()
    }

    val email = "test@gmail.com"
    val password = "password"
    val signForm = SignForm(email, password)
    val requestBody = jacksonObjectMapper().writeValueAsString(signForm)

    Given("특정 이메일로 생성된 계정이 이미 존재하는 경우") {

        every { accountRepository.existsByEmail(any()) } returns true

        When("사용자가 해당 이메일로 회원 가입을 시도한다면") {

            val result = mockMvc
                .perform(
                    post("/account/sign-up").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

            Then("409에러로 응답한다.") {
                result.response.status shouldBe HttpStatus.CONFLICT.value()
            }
        }
    }
    Given("특정 이메일로 생성된 계정이 존재하지 않는 경우") {

        every { accountRepository.existsByEmail(any()) } returns false
        every { accountRepository.save(any()) } returns Account(email, password)

        When("사용자가 해당 이메일로 회원가입을 시도한다면") {

            val result = mockMvc
                .perform(
                    post("/account/sign-up").content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andReturn()

            Then("회원 정보를 db에 저장하고") {
                //TODO: 나중에 H2DB를 이용한 저장 테스트 구현
            }
            Then("상태코드는 200 ok로 응답한다") {
                result.response.status shouldBe HttpStatus.OK.value()
            }
            Then("body는 'status':'success'로 응답한다") {
                val contentMap = JSONObject(result.response.contentAsString)
                contentMap["status"] shouldBe "success"
            }
        }
    }
})