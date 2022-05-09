package com.example.security.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthorizationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (request.servletPath.equals("/api/login")) {
            filterChain.doFilter(request, response)
        } else {
            val authorizationHeader = request.getHeader(AUTHORIZATION)
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    val token = authorizationHeader.split(" ")[1]
                    val algorithm = Algorithm.HMAC256("secret".toByteArray())
                    val verifier = JWT.require(algorithm).build()
                    val decodedJWT = verifier.verify(token)
                    val username = decodedJWT.subject
                    val roles = decodedJWT.getClaim("roles").asArray(String::class.java)
                    val authorities = roles.map { SimpleGrantedAuthority(it) }
                    val authenticationToken = UsernamePasswordAuthenticationToken(username, null, authorities)
                    SecurityContextHolder.getContext().authentication = authenticationToken
                    filterChain.doFilter(request, response)
                } catch (e: java.lang.Exception) {
                    log.error("Error occurred while processing JWT token")
                }
            } else {
                filterChain.doFilter(request, response)
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CustomAuthorizationFilter::class.java)
    }
}