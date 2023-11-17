package xyz.iknowca.iknowdevspring.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.CorsSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.http
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Order(1)
@Configuration
@EnableWebSecurity
open class SecurityConfig(

) {
    @Bean
    open fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            cors {
                configurationSource = CorsConfigurationSource {
                    CorsConfiguration().apply { addAllowedOrigin("http://localhost:9000") }
                }
            }
        }
        return http.build()
    }
}