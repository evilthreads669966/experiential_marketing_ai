package com.limemedia.user.auth

import com.limemedia.user.sessions.cookie.PrincipalSession
import io.ktor.auth.*


internal object PrincipalProvider {
    private val credentials = listOf<UserPasswordCredential>(
        UserPasswordCredential("admin", "password"),
        UserPasswordCredential("limemedia", "password")
    )

    val users = credentials.map { it.name }

    fun tryAuth(credential: UserPasswordCredential): PrincipalSession?{
        val username = credentials.firstOrNull { it.name == credential.name && it.password == credential.password }?.name
            ?: return null
        return PrincipalSession(username)
    }
}