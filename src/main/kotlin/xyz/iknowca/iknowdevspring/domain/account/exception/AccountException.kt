package xyz.iknowca.iknowdevspring.domain.account.exception

import org.springframework.http.HttpStatus

class AccountException(message: String): RuntimeException(){
    lateinit var accountError: AccountError

    enum class AccountError(val status: HttpStatus, val message:String) {
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Your account authorization is not verified.")
    }
}