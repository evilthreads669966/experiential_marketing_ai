package com.limemedia.networking

import com.geotab.api.GeotabApi
import com.geotab.http.exception.InvalidUserException
import com.geotab.model.login.Credentials
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

class Geotab(dbName: String, email: String, pw: String) {
    val api: GeotabApi = GeotabApi(createCredentials(dbName, email, pw))
    val credentials: Credentials

    init {
        try{
            credentials = authenticate()
        }catch (e: InvalidUserException){
            Logger.getGlobal().log(Level.SEVERE, "Credentials are wrong and cannot authenticate")
            exitProcess(1)
        }
    }

   private  fun createCredentials(dbName: String, email: String, pw: String): Credentials {
        return Credentials.builder().apply {
            database(dbName)
            userName(email)
            password(pw)
        }.build()
    }

    @Throws(InvalidUserException::class)
    private fun authenticate(): Credentials = api.authenticate().credentials
}