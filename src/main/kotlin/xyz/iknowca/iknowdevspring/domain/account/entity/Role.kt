package xyz.iknowca.iknowdevspring.domain.account.entity

import jakarta.persistence.*

@Entity
class Role(
    @Enumerated(EnumType.STRING)
    val roleType: RoleType,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) {
}