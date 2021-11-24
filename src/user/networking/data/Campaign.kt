package com.limemedia.user.networking.data

data class Campaign(
    val name: String,
    val token: String,
    val impressions: Int,
    val cpm: Double,
    val adSpend: Double,
    val clicks: Int,
    val ctr: Double,
    val cpa: Double,
    val visits: Int
    )