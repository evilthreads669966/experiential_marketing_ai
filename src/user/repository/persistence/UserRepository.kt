package com.limemedia.user.repository.persistence

import com.limemedia.user.repository.persistence.data.Advertisement
import com.limemedia.user.repository.persistence.data.Client
import com.limemedia.user.repository.persistence.data.User
import org.jetbrains.exposed.sql.SizedIterable

interface UserRepository {
    suspend fun createUser(username: String): String?

    suspend fun createClient(id: String, username: String): String?

    suspend fun createAdvertisement(
        client: String,
        name: String,
        description: String,
        startDate: Long? = null,
        endDate: Long? = null,
    ): Int?

    suspend fun createMedia(advertisementId: Int, uri: String): Int?

    suspend fun findUserById(username: String): User?

    suspend fun findClientsByUser(username: String): List<Client>

    suspend fun findAdById(id: Int): Advertisement?

    suspend fun findAdvertisementsByUser(username: String): List<Advertisement>

    suspend fun findMediaByAdvertisement(advertisement: AdvertisementEntity): SizedIterable<MediaEntity>

    suspend fun updateAd(id: Int, client: String, name: String, description: String, startDate: Long?, endDate: Long?)
}