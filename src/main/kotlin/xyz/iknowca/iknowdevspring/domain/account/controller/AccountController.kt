package xyz.iknowca.iknowdevspring.domain.account.controller

import lombok.RequiredArgsConstructor
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignUpForm
import xyz.iknowca.iknowdevspring.domain.account.service.AccountService

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
class AccountController(
    val accountService: AccountService
) {
    @PostMapping
    fun signUp(@RequestBody signUpForm: SignUpForm):ResponseEntity<Map<String, String>> {
        return accountService.signUp(signUpForm)
    }
}