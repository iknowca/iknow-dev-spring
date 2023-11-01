package xyz.iknowca.iknowdevspring.domain.account.controller

import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignForm
import xyz.iknowca.iknowdevspring.domain.account.service.AccountService
import xyz.iknowca.iknowdevspring.domain.account.service.form.AccountDto

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
class AccountController(
    val accountService: AccountService
) {
    @PostMapping("/sign-up")
    fun signUp(@RequestBody signForm: SignForm):ResponseEntity<Map<String, String>> {
        return accountService.signUp(signForm)
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody signForm: SignForm): ResponseEntity<Map<String, String>> {
        return accountService.signIn(signForm)
    }

    @GetMapping
    fun getAccount(accountId: Long): ResponseEntity<AccountDto> {
        return accountService.getAccountInfo(accountId)
    }
}