package com.limemedia.user.sessions.cookie

import io.ktor.auth.*

// TODO: 6/9/21 replace name with random UUID
data class PrincipalSession(val name: String): Principal{
    fun isAdmin() = this.name == "admin"
}