package xyz.iknowca.iknowdevspring.domain.account.service

import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignUpForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository

@Service
@RequiredArgsConstructor
class AccountServiceImpl(
    private val accountRepository: AccountRepository
):AccountService {
    override fun signUp(signUpForm: SignUpForm): ResponseEntity<Map<String, String>> {
        if(accountRepository.existsByEmail(signUpForm.email)) {
            return ResponseEntity.noContent()
                .build()
        }
        val account = accountRepository.save(Account(signUpForm.email, signUpForm.password))
        return ResponseEntity.ok()
            .body(mapOf("status" to "ok"))
    }
}