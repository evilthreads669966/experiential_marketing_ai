package user.account

import com.limemedia.auth.form.provideFormAuthentication
import com.limemedia.auth.sessions.providePrincipalSession
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.sessions.*

object UserAuth {
    val SESSION = "session_user"
    val FORM = "form_login"
    val FORM_PASSWORD = "password"
    val FORM_USERNAME = "username"
}

fun Application.installUserAccounts(){
    install(Sessions){
        providePrincipalSession()
    }

    install(Authentication){
        provideFormAuthentication()
    }
}