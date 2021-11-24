package com.limemedia.util

import com.limemedia.repository.persistence.db.tables.Locations
import com.limemedia.repository.persistence.db.tables.Vehicles
import com.limemedia.user.repository.persistence.tables.Advertisements
import com.limemedia.user.repository.persistence.tables.Clients
import com.limemedia.user.repository.persistence.tables.Medias
import com.limemedia.user.repository.persistence.tables.Users
import com.limemedia.user.sessions.cookie.PrincipalSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.sessions.*
import kotlinx.css.CSSBuilder
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

fun createTables(){
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Vehicles, Locations, Users, Clients, Advertisements, Medias)
    }
}

fun ApplicationCall.userSession() = this.sessions.get<PrincipalSession>()

fun ApplicationCall.username() = this.userSession()?.name