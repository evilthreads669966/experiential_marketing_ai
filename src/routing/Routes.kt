package com.limemedia

import com.limemedia.service.Service
import com.limemedia.ui.*
import com.limemedia.ui.user.forms.adForm
import com.limemedia.ui.user.showAds
import com.limemedia.user.repository.persistence.data.Advertisement
import com.limemedia.user.service.UserService
import com.limemedia.user.sessions.cookie.PrincipalSession
import com.limemedia.util.respondCss
import com.limemedia.util.userSession
import com.limemedia.util.username
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlinx.coroutines.*
import kotlinx.html.*
import user.account.UserAuth
import java.io.File
import java.io.InputStream
import java.io.OutputStream

suspend fun Route.loginRoutes( service: UserService) {
    get<Login>{
        if(call.userSession() != null){
            call.respondRedirect("/user/ads")
        }
        else{
            call.respondHtmlTemplate(FormListTemplate()){
                this.article{
                    this.heading{ +"Login" }
                    content{
                        loginForm()
                    }
                }
            }
        }
    }

    authenticate(UserAuth.FORM) {
        post("/") {
            val session = call.principal<PrincipalSession>()
            call.sessions.set(session?.copy(name = session.name))
            call.respondRedirect("/user/clients", true)
        }
    }

    get<Logout>{
        with(call){
            if(call.userSession() == null)
                return@with respondRedirect("/")
            sessions.clear<PrincipalSession>()
            principal<PrincipalSession>()
            respondRedirect("/")
        }
    }

    get<StyleSheet> {
        call.respondCss {
            createStyles()
        }
    }
}

suspend fun Route.userRoutes(service: UserService) {
    get<User> {
        val username = call.userSession() ?: return@get call.respondRedirect("/")
        call.respondHtmlTemplate(FormListTemplate()) {
            article{
                content{
                    p { +"Coming soon" }
                }
            }
        }
    }

    get<User.Clients> {
        val username = call.username() ?: return@get call.respondRedirect("/")
        val clients = service.repo.findClientsByUser(username)
        call.respondHtmlTemplate(FormListTemplate()) {
            article{
                content{
                    ul {
                        clients.forEach { client ->
                            li { +client.name }
                        }
                    }
                }
            }
            formContent{
                postForm(action = "/user/clients", encType = FormEncType.applicationXWwwFormUrlEncoded) {
                    name = "form-client"
                    label {
                        +"Create Client"
                        textInput(name = "name") {}
                    }
                    submitInput(name = "submit") {}
                }
            }
        }
    }

    post<User.Clients> {
        call.userSession() ?: return@post call.respondRedirect("/")
        val name = call.receiveParameters()["name"]
            ?: return@post call.respondRedirect("/user/clients", true)
        val username = call.username()
            ?: return@post call.respondRedirect("/", true)

        service.repo.createClient(name, username)
        call.respondRedirect("/user/clients", true)
    }

    get<User.Ads>{
        val username = call.username() ?: return@get call.respondRedirect("/")
        val ads = service.repo.findAdvertisementsByUser(username)
     /*   if(ads.isEmpty())
            return@get call.respondRedirect("/user/ads/create")*/
        call.respondHtmlTemplate(FormListTemplate()){
            article{
                heading{ +"Advertisements" }
                content{
                    showAds(ads)
                }
            }
            formContent{
                adForm()
            }
        }
    }

    post("/user/ads") {
        call.userSession() ?: return@post call.respondRedirect("/")
        var name: String? = null
        var client: String? = null
        var description: String? = null
        var fileName: String? = null
        var uri: String? = null
        val data = call.receiveMultipart()
        data.forEachPart { part ->
            when (part) {
                is PartData.FormItem -> {
                    if(part.value == "name")
                        name = part.value
                    else if(part.value == "client")
                        client = part.value
                    else if(part.value == "description")
                        description = part.value
                    part.dispose
                }
                is PartData.FileItem -> {
                    fileName = part.originalFileName as String
                    uri = fileName
                    val dir = File(Advertisement.UPLOADS_DIR)
                    dir.mkdirs()
                    val file = File(dir, uri)
                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                    part.dispose
                }
            }
        }
        val ad = service.repo.createAdvertisement(client!!, name!!, description!!, null, null)
        if(ad != null && uri != null)
            service.repo.createMedia(ad, uri!!)
        call.respondRedirect("/user/ads", true)
    }

    get<User.Ads.Edit>{
        call.userSession() ?: return@get call.respondRedirect("/")
        val id = it.id
        val ad = runBlocking { service.repo.findAdById(id) }
        call.respondHtmlTemplate(SingleColumnTemplate()){
            column{
                if(ad != null)
                    adForm(ad)
                else
                    p { +"Sorry. That ad does not exist yet." }
            }
        }
    }

    post<User.Ads.Edit>{
        call.userSession() ?: return@post call.respondRedirect("/",)
        call.receiveParameters().run {
            it.name = this["name"]?.trim()
            it.client = this["client"]?.trim()
            it.description = this["description"]?.trim()
            runCatching { it.start = this["start"]?.trim()?.toLong() }
            runCatching { it.end = this["end"]?.trim()?.toLong() }
        }
        service.repo.updateAd(it.id, it.client!!, it.name!!, it.description!!, it.start, it.end)
        call.respondRedirect("/user/ads", true)
    }
}

suspend fun Route.adminConsoleRoutes(service: Service){
    get<Admin.Vehicles> { vehicle ->
/*        val isAdmin = call.sessions.get<PrincipalSession>()?.isAdmin()
            ?: return@get call.respondRedirect("/")
        if(!isAdmin)
            return@get call.respondRedirect("/")*/
        call.respondVehicles(service)
    }

    get<Admin.Vehicles.Locations> { vehicle ->
/*        val isAdmin = call.sessions.get<PrincipalSession>()?.isAdmin()
            ?: return@get call.respondRedirect("/")
        if(!isAdmin)
            return@get call.respondRedirect("/")*/
        call.respondVehicleLocations(service, vehicle.id)
    }
}

suspend fun Route.apiRoutes(service: Service){
    get<Api.Vehicles> {
        val vehicles = service.findVehicles()
        if (vehicles.isEmpty())
            call.respond(HttpStatusCode.NoContent, "No vehicles exist!")
        else
            call.respond(vehicles)
    }

    get<Api.Vehicle> { vehicle ->
        val vehicle = service.findVehicle(vehicle.id)
            ?: return@get call.respond(HttpStatusCode.NotFound, "No vehicle exists!")
        call.respond(vehicle)
    }

    get<Api.Vehicle.Locations> { locationQuery ->
        with(locationQuery) {
            val locations = service.findLocations(vehicle.id, from, to, limit)
            if (locations.isEmpty())
                call.respond(HttpStatusCode.NoContent, "No locations exist!")
            else
                call.respond(locations)
        }
    }
}

suspend fun ApplicationCall.respondVehicles(service: Service) {
    val vehicles = service.findVehicles()
    if (vehicles.isEmpty())
        respond(HttpStatusCode.NoContent, "No vehicles exist!")
    else
        respondHtmlTemplate(SingleColumnTemplate()) {
            column{
                showVehicles(vehicles)
            }
        }
}

suspend fun ApplicationCall.respondVehicleLocations(service: Service, id: String) {
    val locations = service.findLocations(id)
    if (locations.isEmpty())
        respond(HttpStatusCode.NoContent, "No locations exist!")
    respondHtmlTemplate(SingleColumnTemplate()){
        column{
            showLocations(id, locations)
        }
    }
}


suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}