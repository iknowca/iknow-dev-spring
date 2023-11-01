package xyz.iknowca.iknowdevspring.domain.account.service

import org.springframework.http.ResponseEntity
import xyz.iknowca.iknowdevspring.domain.account.controller.form.SignUpForm
import xyz.iknowca.iknowdevspring.domain.account.entity.Account

interface AccountService {
    fun signUp(signUpForm: SignUpForm): ResponseEntity<Map<String, String>>
}