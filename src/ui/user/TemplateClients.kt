package com.limemedia.ui.user

import com.limemedia.user.repository.persistence.data.Advertisement
import com.limemedia.user.repository.persistence.data.Client
import kotlinx.html.*

fun FlowContent.showClients(clients: List<Client>){
    h3 { +"Clients" }
    if(clients.isEmpty())
        p { +"No clients yet." }
    else
        ul {
            clients.forEach { client ->
                li { +"${client.name}" }
            }
        }
}

fun FlowContent.showAds(ads: List<Advertisement>){
    table(classes = "center") {
        thead {
            tr {
                td { +"Ads" }
            }
        }
        tr {
            th { + "Name" }
            th { + "Client" }
            th { +"Description" }
            th { + "Created" }
            th { + "Starting At" }
            th { + "Ending At" }
            th { + "Impressions" }
            th { +"Status" }
            th { + "Edit Ad" }
        }
        ads.forEach { ad ->
            tr {
                td { +ad.name }
                td { +ad.clientId }
                td { +ad.description }
                td { +"${ad.createdAt}" }
                td { +"${ad.startDate ?: ""}" }
                td { +"${ad.endDate ?: ""}" }
                td { +"${ad.impressions}" }
                td { a(href = "/user/ads/edit/${ad.id}") { +"Edit" } }
            }
        }
    }
}