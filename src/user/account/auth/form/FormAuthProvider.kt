package com.limemedia.auth.form

import com.limemedia.user.auth.PrincipalProvider
import com.limemedia.util.username
import io.ktor.auth.*
import user.account.UserAuth

fun Authentication.Configuration.provideFormAuthentication(){
    form(UserAuth.FORM){
        skipWhen { call ->  call.username() != null }
        userParamName = UserAuth.FORM_USERNAME
        passwordParamName = UserAuth.FORM_PASSWORD
        challenge("/")
        validate { credential -> PrincipalProvider.tryAuth(credential) }
    }
}