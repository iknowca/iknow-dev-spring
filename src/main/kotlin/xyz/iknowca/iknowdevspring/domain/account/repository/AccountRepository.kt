package xyz.iknowca.iknowdevspring.domain.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xyz.iknowca.iknowdevspring.domain.account.entity.Account

interface AccountRepository:JpaRepository<Account, Long> {
    fun existsByEmail(email:String):Boolean
    @Query("SELECT a FROM Account a WHERE a.email=:email AND a.password=:password")
    fun findByEmailAndPassword(email: String, password: String):Account?
}