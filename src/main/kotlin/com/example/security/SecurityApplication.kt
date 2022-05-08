package com.example.security

import com.example.security.models.Role
import com.example.security.models.User
import com.example.security.services.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootApplication
class SecurityApplication {
	@Bean
	fun passwordEncoder() = BCryptPasswordEncoder()

	@Bean
	fun init(userService: UserService) = CommandLineRunner {
		with(userService) {
			saveRole(Role(name = "ROLE_USER"))
			saveRole(Role(name = "ROLE_MANAGER"))
			saveRole(Role(name = "ROLE_ADMIN"))
			saveRole(Role(name = "ROLE_SUPER_ADMIN"))

			saveUser(User(name = "John Travolta", username = "john", password = "1234", roles = mutableListOf()))
			saveUser(User(name = "Will Smith", username = "will", password = "1234", roles = mutableListOf()))
			saveUser(User(name = "Jim Carry", username = "jim", password = "1234", roles = mutableListOf()))
			saveUser(User(name = "Arnold Schwarzenegger", username = "arnold", password = "1234", roles = mutableListOf()))

			addRoleToUser("john", "ROLE_USER")
			addRoleToUser("john", "ROLE_MANAGER")
			addRoleToUser("will", "ROLE_MANAGER")
			addRoleToUser("jim", "ROLE_ADMIN")
			addRoleToUser("arnold", "ROLE_SUPER_ADMIN")
			addRoleToUser("arnold", "ROLE_ADMIN")
			addRoleToUser("arnold", "ROLE_USER")
		}
	}
}

fun main(args: Array<String>) {
	runApplication<SecurityApplication>(*args)
}
