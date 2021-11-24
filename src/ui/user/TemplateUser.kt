package com.limemedia.ui

import com.limemedia.user.repository.persistence.data.Advertisement
import kotlinx.html.*

fun FlowContent.showUser(username: String, advertisements: List<Advertisement>){
    p { +"Welcome, $username" }
    ul {
        advertisements.forEach { ad ->
            li { a("/user/advertisement/${ad.id}") { +ad.name } }
        }
    }
}