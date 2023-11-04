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
class SignUpApiTest : BehaviorSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        val accountRepository = mockk<AccountRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(AccountController(AccountServiceImpl(accountRepository))).build()

        val email = "test@gmail.com"
        val password = "password"
        val signForm = SignForm(email, password)
        val requestBody = jacksonObjectMapper().writeValueAsString(signForm)

        Given("특정 이메일로 생성된 계정이 이미 존재하는 경우") {

            every { accountRepository.existsByEmail(any()) } returns true

            When("사용자가 해당 이메일로 회원 가입을 시도한다면") {

                val result = mockMvc.post("/account/sign-up") {
                    content = requestBody
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }

                Then("409에러로 응답한다.") {
                    result.andExpect {
                        status { isConflict() }
                    }
                }
            }
        }
        Given("특정 이메일로 생성된 계정이 존재하지 않는 경우") {

            every { accountRepository.existsByEmail(any()) } returns false
            every { accountRepository.save(any()) } returns Account(email, password)

            When("사용자가 해당 이메일로 회원가입을 시도한다면") {

                val result = mockMvc.post("/account/sign-up") {
                    content = requestBody
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }
                Then("회원 정보를 db에 저장하고") {
                    //TODO: 나중에 H2DB를 이용한 저장 테스트 구현
                }
                Then("상태코드는 200 ok로 응답한다") {
                    result.andExpect {
                        status { isOk() }
                    }
                }
                Then("body는 'status':'success'로 응답한다") {
                    result.andExpect {
                        jsonPath("$.status") { value("success" )}
                    }
                }
            }
        }
    }
}