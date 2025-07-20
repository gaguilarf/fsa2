package com.molytech.fsa.domain.entities

data class User(
    val email: String,
    val name: String,
    val role: String,
    val phone: String = "",
    val year: String = "",
    val carBrand: String = ""
)
