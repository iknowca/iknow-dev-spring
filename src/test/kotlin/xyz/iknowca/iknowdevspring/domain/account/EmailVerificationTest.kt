package xyz.iknowca.iknowdevspring.domain.account

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl

class EmailVerificationTest : BehaviorSpec() {

    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    init {
        val accountRepository = mockk<AccountRepository>()
        val mockMvc = MockMvcBuilders.standaloneSetup(AccountController(AccountServiceImpl(accountRepository))).build()

        Given("특정 이메일로 가입한 계정이 있는지 확인하는 요청이 들어올 경우") {
            val email: String = "exampleTest@test.com"
            val nonExsistEmail: String = "exampleTest@teste.com"

            every { accountRepository.existsByEmail(email) } returns true
            every { accountRepository.existsByEmail(nonExsistEmail) } returns false

            When("해당 이메일로 가입한 계정이 존재하지 않는 다면") {
                val result = mockMvc.get("/account/email-verification/$nonExsistEmail")
                Then("status code: 200 ok") {
                    result.andExpect { status { isOk() } }
                }
                Then("message: false") {
                    result.andExpect {
                        content { false }

                    }
                }
            }
            When("해당 이메일로 가입한 계정이 존재한다면") {
                val result = mockMvc.get("/account/email-verification/$email")
                Then("status code: 200 ok") {
                    result.andExpect {
                        status { isOk() }
                    }
                }
                Then("message: true") {
                    result.andExpect { content { true } }
                }
            }
        }
    }
}
