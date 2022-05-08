package com.example.security.services

import com.example.security.models.Role
import com.example.security.models.User

interface UserService {
    fun saveUser(user: User): User
    fun saveRole(role: Role): Role
    fun addRoleToUser(username: String, roleName: String)
    fun getUser(username: String): User
    fun getUsers(): List<User>
}