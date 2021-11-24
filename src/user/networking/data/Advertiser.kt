package com.limemedia.user.networking.data

data class Advertiser(
    val name: String,
    val token : String,
    val campaigns: List<Campaign>
)