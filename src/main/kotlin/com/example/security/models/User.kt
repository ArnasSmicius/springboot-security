package com.example.security.models

import javax.persistence.*
import javax.persistence.GenerationType.AUTO

@Entity
data class User(
    @Id @GeneratedValue(strategy = AUTO)
    val id: Long,
    val name: String,
    val userName: String,
    val password: String,
    @ManyToMany(fetch = FetchType.EAGER)
    val roles: MutableList<Role> = mutableListOf(),
)
