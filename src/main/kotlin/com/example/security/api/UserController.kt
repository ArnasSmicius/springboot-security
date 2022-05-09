package com.example.security.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.security.models.Role
import com.example.security.models.User
import com.example.security.services.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/api")
class UserController @Autowired constructor(
    private val userService: UserService
) {
    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<User>> {
        return ResponseEntity.ok().body(userService.getUsers())
    }

    @PostMapping("/users")
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users").toUriString())
        return ResponseEntity.created(uri).body(userService.saveUser(user))
    }

    @PostMapping("/roles")
    fun createRole(@RequestBody role: Role): ResponseEntity<Role> {
        val uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/roles").toUriString())
        return ResponseEntity.created(uri).body(userService.saveRole(role))
    }

    @PostMapping("/roles/bind")
    fun bindRole(@RequestBody request: BindRoleRequest): ResponseEntity<Unit> {
        userService.addRoleToUser(request.username, request.role)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/token/refresh")
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse) {
        val authorizationHeader = request.getHeader(AUTHORIZATION)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                val refreshToken = authorizationHeader.split(" ")[1]
                val algorithm = Algorithm.HMAC256("secret".toByteArray())
                val verifier = JWT.require(algorithm).build()
                val decodedJWT = verifier.verify(refreshToken)
                val username = decodedJWT.subject
                val user = userService.getUser(username)

                val accessToken = JWT.create()
                    .withSubject(user.username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .withIssuer(request.requestURL.toString())
                    .withClaim("roles", user.roles.map { it.name })
                    .sign(algorithm)


                val tokens = mapOf("access_token" to accessToken, "refresh_token" to refreshToken)
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, tokens)
            } catch (e: java.lang.Exception) {
                log.error("Error logging in: ${e.message}")
                response.setHeader("error", e.message)
                response.status = HttpStatus.FORBIDDEN.value()
//                    response.sendError(HttpStatus.FORBIDDEN.value())
                val tokens = mapOf("error_message" to e.message)
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                ObjectMapper().writeValue(response.outputStream, tokens)
            }
        } else {
            throw RuntimeException("Refresh token is missing")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserController::class.java)
    }
}

data class BindRoleRequest(
    val username: String,
    val role: String
)