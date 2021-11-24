package com.limemedia.user.networking

import com.geotab.model.serialization.DateTimeSerializationUtil
import com.limemedia.user.networking.data.Advertiser
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.time.ZoneId

import java.time.format.DateTimeFormatter
import java.util.*


val partnerToken = "secret"

interface AgilityService {
    suspend fun authenticate(): AuthenticatonResponse

    suspend fun getPartnerMetrics(jwt: String): String
}

class AgilityServiceImpl: AgilityService{
    val client: HttpClient

    init {
        client = HttpClient(CIO) {
            install(JsonFeature){
                serializer = GsonSerializer(){
                    disableHtmlEscaping()
                    setPrettyPrinting()
                }
            }
        }
    }

    override suspend fun authenticate(): AuthenticatonResponse {
        return client.post<AuthenticatonResponse>("https://rpt.agilityads.com/v1_noauth/sign_in"){
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
            }
            contentType(ContentType.Application.Json)
            body = AuthenticationRequest("jeff.cline@me.com", "Agility!234")
        }
    }

    override suspend fun getPartnerMetrics(jwt: String): String {
        return client.post("https://rpt.agilityads.com/v1/partner/campaign_metrics"){
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json)
                append(HttpHeaders.Authorization, jwt)
            }
            contentType(ContentType.Application.Json)
            val from = DateTimeSerializationUtil.nowUtcLocalDateTime().minusYears(1).format(formatter)
            val to = DateTimeSerializationUtil.nowUtcLocalDateTime().format(formatter)
            body = PartnerRequest(UUID.fromString(partnerToken), from, to)
        }
    }
}

data class AuthenticationRequest(val email_addr: String, val pwd_one: String)

data class AuthenticatonResponse(val token: String, val email: String, val active: Boolean, val jwt: String)

data class PartnerRequest(val partnerToken: UUID, val startDate: String, val endDate: String)

data class PartnerResponse(val advertisers: List<Advertiser>)

var formatter = DateTimeFormatter
    .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    .withZone(ZoneId.of("UTC"))