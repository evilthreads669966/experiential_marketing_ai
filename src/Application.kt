package com.limemedia

import com.limemedia.service.Service
import com.limemedia.user.auth.PrincipalProvider
import com.limemedia.user.di.UserModules
import com.limemedia.user.networking.AgilityService
import com.limemedia.user.service.UserService
import com.limemedia.util.createTables
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.hc.client5.http.auth.AuthenticationException
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level
import user.account.installUserAccounts
import javax.sql.DataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(StatusPages){
        exception<AuthenticationException>{
            call.respond(HttpStatusCode.Unauthorized)
        }
        exception<Throwable> { exception ->
            call.respond(HttpStatusCode.BadRequest, "Something is wrong with the request.")
        }
    }

    install(DefaultHeaders)

    install(ConditionalHeaders)

    install(Compression)

    install(CORS){
        allowCredentials = true
    }

    install(CallLogging){
        level = Level.INFO
    }

    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }

    installUserAccounts()

    install(Locations)

    install(Koin){
        modules(AppModules.dataAccessModule, AppModules.dataSourceModule, UserModules.dataAccessModule)
    }

    val service by inject<Service>()
    val db by inject<DataSource>()
    val userService by inject<UserService>()
    val agilityService by inject<AgilityService>()

    launch {
        val auth = agilityService.authenticate()
        val partner = agilityService.getPartnerMetrics(auth.jwt)
        System.out.println(partner.toString())
    }
    Database.connect(db)

    createTables()

    launch(Dispatchers.IO) {
        val users = PrincipalProvider.users
        users.forEach {
            userService.repo.createUser(it)
        }
     //   service.addAllVehiclesAndLocations(this)
    }

    routing {
        launch(Dispatchers.IO){
            loginRoutes(userService)
            adminConsoleRoutes(service)
            apiRoutes(service)
            userRoutes(userService)
        }
    }
}