package xyz.iknowca.iknowdevspring.domain.account.service

import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.repository.AccountRepository
import xyz.iknowca.iknowdevspring.domain.account.service.form.AccountDto

@Service
@RequiredArgsConstructor
class AccountServiceImpl(
    private val accountRepository: AccountRepository
):AccountService {
    override fun signUp(signForm: SignForm): ResponseEntity<Map<String, String>> {
        if(accountRepository.existsByEmail(signForm.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
        val account:Account = accountRepository.save(Account(signForm.email, signForm.password))
        return ResponseEntity.ok()
            .body(mapOf("status" to "success"))
    }

    override fun signIn(signForm: SignForm): ResponseEntity<Map<String, String>> {
        val savedAccount = accountRepository.findByEmailAndPassword(signForm.email, signForm.password)
            ?: return ResponseEntity.noContent().build()

        return ResponseEntity.ok()
            .body(mapOf("status" to "success", "accountId" to savedAccount.id.toString()))
    }

    override fun getAccountInfo(accountId: Long): ResponseEntity<AccountDto> {
        val maybeAccount = accountRepository.findById(accountId)
        if (maybeAccount.isEmpty) {
            return ResponseEntity.noContent().build()
        }

        return ResponseEntity.ok(AccountDto(maybeAccount.get().email))
    }
}