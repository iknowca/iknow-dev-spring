package xyz.iknowca.iknowdevspring.account

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignUpForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountService
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl

/**
 * [회원 가입 테스트]
 * 1. 회원가입이 정상적으로 동작한다.
 * 2. 중복되는 email로는 가입할 수 없다.
 */

class SignUpTest : BehaviorSpec({
    lateinit var accountRepository: AccountRepository
    lateinit var accountController: AccountController


    given("유저가 회원가입을 요청을 할 때") {
        accountRepository = mockk()
        accountController = AccountController(AccountServiceImpl(accountRepository))

        val email = "email@test.com"
        val password = "some_secure_password"
        val signUpForm: SignUpForm = SignUpForm(email, password)

        `when`("이메일이 중복되지 않았다면") {
            every { accountRepository.existsByEmail(email) } returns false
            every { accountRepository.save(any()) } returns Account(email, password)
            val response = accountController.signUp(signUpForm)

            then("회원 정보를 db에 저장한다.") {
                response.body.let { body -> body!!["status"] shouldBe "ok" }
            }
        }
        `when`("중복된 이메일이 존재한다면") {
            every { accountRepository.existsByEmail(email) } returns true
            val response = accountController.signUp(signUpForm)

            then("null을 반환한다.") {
                response.body shouldBe null
            }
        }
    }

})