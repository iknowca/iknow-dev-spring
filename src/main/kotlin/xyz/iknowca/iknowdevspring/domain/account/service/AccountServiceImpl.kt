package xyz.iknowca.iknowdevspring.domain.account.service

import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.exception.AccountException
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
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        return ResponseEntity.ok()
            .body(mapOf("status" to "success", "accountId" to savedAccount.id.toString()))
    }

    override fun getAccountInfo(auth: String): ResponseEntity<AccountDto> {
        val maybeAccount = accountRepository.findById(auth.toLong())
        if (maybeAccount.isEmpty) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        return ResponseEntity.ok(AccountDto(maybeAccount.get().email))
    }

    override fun findAccount(authorization: String): Account {
        val maybeAccount = accountRepository.findById(authorization.toString().toLong())
        if (maybeAccount.isEmpty) {
            throw AccountException()
        }
        return maybeAccount.get()
    }

    override fun checkRole(account: Account, roleType: RoleType): Boolean {
        val roles:List<RoleType> = accountRepository.findRoleByAccount(account)
        return roleType in roles
    }

    override fun exsistEmail(email: String): ResponseEntity<Boolean> {
        val exist = accountRepository.existsByEmail(email)
        return ResponseEntity.ok(exist)
    }
}