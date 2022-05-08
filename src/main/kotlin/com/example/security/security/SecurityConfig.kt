package com.example.security.security

import com.example.security.filter.CustomAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) : WebSecurityConfigurerAdapter() {
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder)

    }

    override fun configure(http: HttpSecurity) {
        val customAuthenticationFilter = CustomAuthenticationFilter(authenticationManagerBean())
        customAuthenticationFilter.setFilterProcessesUrl("/api/login")
        http.csrf().disable()
        http.sessionManagement().sessionCreationPolicy(STATELESS)
        http.authorizeRequests().antMatchers("/api/login").permitAll()
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/users/**").hasAnyAuthority("ROLE_USER")
        http.authorizeRequests().antMatchers(HttpMethod.GET,"/api/roles/**").hasAnyAuthority("ROLE_ADMIN")
        http.authorizeRequests().anyRequest().authenticated()
        http.addFilter(customAuthenticationFilter)
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }
}