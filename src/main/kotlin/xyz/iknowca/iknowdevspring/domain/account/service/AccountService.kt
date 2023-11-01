package xyz.iknowca.iknowdevspring.domain.account.service

import org.springframework.http.ResponseEntity
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.service.form.AccountDto

interface AccountService {
    fun signUp(signForm: SignForm): ResponseEntity<Map<String, String>>
    fun signIn(signForm: SignForm): ResponseEntity<Map<String, String>>
    fun getAccountInfo(accountId: Long): ResponseEntity<AccountDto>
}