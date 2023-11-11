package xyz.iknowca.iknowdevspring.domain.account.entity

import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority

@Entity
class Account(
    val email: String,
    var password: String,
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    val roles: List<Role> = listOf(Role(RoleType.NORMAL)),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {

}