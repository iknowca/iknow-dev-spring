package xyz.iknowca.iknowdevspring.domain.account.service

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.service.form.AccountDto

interface AccountService {
    fun signUp(signForm: SignForm): ResponseEntity<Map<String, String>>
    fun signIn(signForm: SignForm): ResponseEntity<Map<String, String>>
    fun getAccountInfo(auth: String): ResponseEntity<AccountDto>
    fun findAccount(authorization: String): Account
    fun checkRole(account: Account, roleType: RoleType): Boolean
}