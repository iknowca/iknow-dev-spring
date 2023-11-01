package xyz.iknowca.iknowdevspring.account

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl
import java.util.*

class AccountTest:BehaviorSpec({
    lateinit var accountRepository: AccountRepository
    lateinit var accountController: AccountController

    beforeTest {
        accountRepository = mockk()
        accountController = AccountController(AccountServiceImpl(accountRepository))

    }

    Given("회원이 accountId를 이용해 회원 정보를 요청할 때") {


        val accountId = 0L

        val email = "test@test.com"
        val password = "temp_password"

        When("accountId가 존재하지 않는경우") {
            every { accountRepository.findById(accountId) } returns Optional.ofNullable(null)
            val response = accountController.getAccount(accountId)
            Then("null을 반환한다.") {
                response.body shouldBe null
            }
        }
        When("accountId가 존재할 경우") {
            every { accountRepository.findById(accountId) } returns Optional.of(Account(email, password))
            val response = accountController.getAccount(accountId)
            Then("회원 정보를 반환한다.") {
                response.body.email shouldBe email
            }
        }
    }
}) {
}