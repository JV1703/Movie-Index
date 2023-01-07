package com.example.movieindex.core.data.remote.model.auth.body

data class LoginBody(
    val username: String,
    val password: String,
    val request_token: String,
)