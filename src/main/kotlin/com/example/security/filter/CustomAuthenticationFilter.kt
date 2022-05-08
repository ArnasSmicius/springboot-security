package com.example.security.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Date
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationFilter(
    private val authManager: AuthenticationManager
) : UsernamePasswordAuthenticationFilter() {
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse?): Authentication {
        val username = request.getParameter("username")
        val password = request.getParameter("password")
        log.info("Username is: $username, password is: $password")
        val authenticationToken = UsernamePasswordAuthenticationToken(username, password)
        return authManager.authenticate(authenticationToken)
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain?,
        authResult: Authentication
    ) {
        val user = authResult.principal as User
        val algorithm = Algorithm.HMAC256("secret".toByteArray())
        val accessToken = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
            .withIssuer(request.requestURL.toString())
            .withClaim("roles", user.authorities.map { it.authority })
            .sign(algorithm)
        val refreshToken = JWT.create()
            .withSubject(user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + 30 * 60 * 1000))
            .withIssuer(request.requestURL.toString())
            .sign(algorithm)

        response.setHeader("access_token", accessToken)
        response.setHeader("refresh_token", refreshToken)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomAuthenticationFilter::class.java)
    }
}