package xyz.iknowca.iknowdevspring.config

import jakarta.annotation.PostConstruct
import lombok.RequiredArgsConstructor
import mu.KotlinLogging
import org.springframework.stereotype.Component
import xyz.iknowca.iknowdevspring.domain.account.entity.Role
import xyz.iknowca.iknowdevspring.domain.account.entity.RoleType
import xyz.iknowca.iknowdevspring.domain.account.repository.RoleRepository

@Component
@RequiredArgsConstructor
class DBInitializer(
    val roleRepository: RoleRepository
) {

    val log = KotlinLogging.logger {}
    @PostConstruct
    fun init() {
        initAccountRoleTypes()
    }

    fun initAccountRoleTypes() {
        try {
            val roles:Set<RoleType>  = roleRepository.findAll().map { it.roleType }.toSet()
            for(role in RoleType.values()) {
                if(!roles.contains(role)) {
                    val role:Role = Role(role)
                    roleRepository.save(role)
                }
            }
        } catch (e:Exception) {
            log.info(e.message, e)
        }
    }
}
