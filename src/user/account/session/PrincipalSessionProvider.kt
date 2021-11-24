package com.limemedia.auth.sessions

import com.limemedia.user.sessions.cookie.PrincipalSession
import io.ktor.sessions.*
import io.ktor.util.*
import user.account.UserAuth
import java.io.File

const val SESSION_PRINCIPAL = "session_principal"

/*stores the username in the cookie*/
fun Sessions.Configuration.providePrincipalSession(){
        cookie<PrincipalSession>(UserAuth.SESSION, storage = directorySessionStorage(File(".users"), cached = true)){
            cookie.extensions["SameSite"] = "lax"
            cookie.path = "/"
            cookie.httpOnly = true
            val secretSignKey = hex("000102030405060708090a0b0c0d0e0f")
            transform(SessionTransportTransformerMessageAuthentication(secretSignKey))
        }
}