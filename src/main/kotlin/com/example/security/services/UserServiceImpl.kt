package com.example.security.services

import com.example.security.models.Role
import com.example.security.models.User
import com.example.security.repositories.RoleRepository
import com.example.security.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
) : UserService, UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val optionalUser = userRepository.findByUsername(username)
        if (optionalUser.isPresent) {
            log.info("User found in the database: $username")
            val user = optionalUser.get()
            return org.springframework.security.core.userdetails.User(
                user.username,
                user.password,
                user.roles.map {role ->
                    SimpleGrantedAuthority(role.name)
                }
            )
        } else {
            log.error("User not found in the database")
            throw UsernameNotFoundException("User not found in the database")
        }
    }
    override fun saveUser(user: User): User {
        log.info("Saving new user ${user.name} to the database")
        return userRepository.save(user)
    }

    override fun saveRole(role: Role): Role {
        log.info("Saving new role ${role.name} to the database")
        return roleRepository.save(role)
    }

    override fun addRoleToUser(username: String, roleName: String) {
        log.info("Adding role $roleName to user $username")
        val user = userRepository.findByUsername(username)
        val role = roleRepository.findByName(roleName)
        user.get().roles.add(role)
    }

    override fun getUser(username: String): User {
        log.info("Fetching user $username")
        return userRepository.findByUsername(username).get()
    }

    override fun getUsers(): List<User> {
        log.info("Fetching all the users")
        return userRepository.findAll()
    }

    companion object {
        private val log = LoggerFactory.getLogger(UserServiceImpl::class.java)
    }
}