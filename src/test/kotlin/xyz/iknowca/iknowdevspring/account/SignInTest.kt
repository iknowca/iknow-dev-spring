package xyz.iknowca.iknowdevspring.account

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import xyz.iknowca.iknowdevspring.domain.account.controller.AccountController
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignUpForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.AccountServiceImpl

/**
 * 회원가입 테스트
 * 1. 이메일과 비밀번호가 일치하는 계정이 존재하는지 확인한다.
 * 2. 존재한다면 refreshToken 과 accessToken을 return한다.
 * 2. 일단은 id를 반환한다.
 * 3. 존재하지 않는다면 null 리턴한다.
 */

class SignInTest: BehaviorSpec({
    lateinit var accountRepository: AccountRepository
    lateinit var accountController: AccountController

    Given("사용자가 로그인을 요청할 때") {
        accountRepository = mockk()
        accountController = AccountController(AccountServiceImpl(accountRepository))

        val email = "test@test.com"
        val password = "password"
        val signUpForm = SignUpForm(email, password)

        When ("db에 일치하는 이메일과 비밀번호가 일치하는 계정이 존재한다면") {

            every { accountRepository.findByEmailAndPassword(email, password) } returns Account(email, password)
            val response = accountController.signIn(signUpForm)

            Then ("accountId를 반환한다.") {
                response.body?.get("accountId") shouldNotBe  null
                response.body?.get("status") shouldBe "success"
            }
        }
        When ("db에 이메일과 비밀번호가 일치하는 계정이 존재하지 않는다면") {
            every { accountRepository.findByEmailAndPassword(email, password) } returns null
            val response = accountController.signIn(signUpForm)

            Then ( "null을 반환한다.") {
                response.body shouldBe null
            }
        }
    }
}) {
}