package com.limemedia.user.repository.persistence

import com.geotab.model.serialization.DateTimeSerializationUtil
import com.limemedia.user.repository.persistence.data.Advertisement
import com.limemedia.user.repository.persistence.data.Client
import com.limemedia.user.repository.persistence.data.User
import com.limemedia.user.repository.persistence.tables.Advertisements
import com.limemedia.user.repository.persistence.tables.Clients
import com.limemedia.user.repository.persistence.tables.Medias
import com.limemedia.user.repository.persistence.tables.Users
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.ZoneOffset

class UserRepositoryImpl: UserRepository {

    override suspend fun createUser(username: String): String?{
        return transaction {
            if(UserEntity.findById(EntityID(username, Users)) != null)
                return@transaction null
            val user = UserEntity.new(username) {
                this.createdAt = DateTimeSerializationUtil.nowUtcLocalDateTime().toEpochSecond(ZoneOffset.UTC)
            }
            user.id.value
        }
    }

    override suspend fun createClient(id: String, username: String): String?{
        return transaction {
            if(ClientEntity.findById(id) != null) return@transaction null

            val client = ClientEntity.new(id) {
                this.username = EntityID(username, Users)
            }
            client.id.value
        }
    }

    override suspend fun createAdvertisement(client: String, name: String, description: String, startDate: Long?, endDate: Long?): Int? {
        val ad = transaction {
            AdvertisementEntity.new {
                this.client = EntityID(client, Clients)
                this.name = name
                this.description = description
                this.createdAt = DateTimeSerializationUtil.nowUtcLocalDateTime().toEpochSecond(ZoneOffset.UTC)
                this.startDate = startDate
                this.endDate = endDate
            }
        }
        return ad.id.value
    }

    override suspend fun createMedia(advertisementId: Int, uri: String): Int? {
        return transaction {
            MediaEntity.new {
                this.advertisement = EntityID(advertisementId, Advertisements)
                this.uri = uri
                this.createdAt = DateTimeSerializationUtil.nowUtcLocalDateTime().toEpochSecond(ZoneOffset.UTC)
            }.id.value
        }
    }

    override suspend fun updateAd(id: Int, client: String, name: String, description: String, startDate: Long?, endDate: Long?) {
        transaction {
            AdvertisementEntity.findById(EntityID(id, Advertisements))?.apply {
                this.client = EntityID(client, Clients)
                this.name = name
                this.description = description
                this.startDate = startDate
                this.endDate = endDate
            }
        }
    }

    override suspend fun findUserById(username: String): User? {
        return transaction { UserEntity.findById(username)?.dto() }
    }

    override suspend fun findClientsByUser(username: String): List<Client> {
        return transaction { ClientEntity.find { Clients.username eq  EntityID(username, Users)}.map{ it.dto() } }
    }

    override suspend fun findAdvertisementsByUser(username: String): List<Advertisement> {
        return transaction{
            val clients = ClientEntity.find { Clients.username eq EntityID(username, Users) }.map { it.id.value }
            AdvertisementEntity.find { Advertisements.client inList clients }.map { it.dto() }
        }
    }

    override suspend fun findAdById(id: Int): Advertisement? {
        val ad = transaction {
            AdvertisementEntity.findById(EntityID(id, Advertisements))
        }
        return ad?.dto()
    }

    override suspend fun findMediaByAdvertisement(advertisement: AdvertisementEntity): SizedIterable<MediaEntity> {
        return transaction{ MediaEntity.find { Medias.advertisement eq advertisement.id } }
    }
}