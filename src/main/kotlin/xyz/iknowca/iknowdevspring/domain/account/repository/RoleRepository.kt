package xyz.iknowca.iknowdevspring.domain.account.repository

import org.springframework.data.jpa.repository.JpaRepository
import xyz.iknowca.iknowdevspring.domain.account.entity.Role

interface RoleRepository:JpaRepository<Role, Long> {
}