package com.example.security.api

import com.example.security.models.Role
import com.example.security.models.User
import com.example.security.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.net.URI

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
}

data class BindRoleRequest(
    val username: String,
    val role: String
)