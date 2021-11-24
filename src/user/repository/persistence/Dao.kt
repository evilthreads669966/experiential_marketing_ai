package com.limemedia.user.repository.persistence

import com.limemedia.user.repository.persistence.data.Advertisement
import com.limemedia.user.repository.persistence.data.Client
import com.limemedia.user.repository.persistence.data.Media
import com.limemedia.user.repository.persistence.data.User
import com.limemedia.user.repository.persistence.tables.Advertisements
import com.limemedia.user.repository.persistence.tables.Clients
import com.limemedia.user.repository.persistence.tables.Medias
import com.limemedia.user.repository.persistence.tables.Users
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

abstract class StringEntityClass<out E: Entity<String>>(table: IdTable<kotlin.String>, entityType: Class<E>? = null) : EntityClass<kotlin.String, E>(table, entityType)

class UserEntity(id: EntityID<String>): Entity<String>(id){
    companion object: StringEntityClass<UserEntity>(Users)
    var createdAt by Users.createdAt

    fun dto() = User(id.value, createdAt)
}

class ClientEntity(id: EntityID<String>): Entity<String>(id){
    companion object: StringEntityClass<ClientEntity>(Clients)
    var username by Clients.username

    fun dto() = Client(id.value, username.value)
}

class AdvertisementEntity(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<AdvertisementEntity>(Advertisements)
    var client by Advertisements.client
    var name by Advertisements.name
    var description by Advertisements.description
    var createdAt by Advertisements.createdAt
    var startDate by Advertisements.startDate
    var endDate by Advertisements.endDate
    var impressions by Advertisements.impressions
    //val media by MediaEntity referrersOn Medias.advertisement

    fun dto() = Advertisement(id.value, client.value, name, description, createdAt, startDate, endDate, impressions)
}

class MediaEntity(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<MediaEntity>(Medias)
    var advertisement by Medias.advertisement
    var uri by Medias.uri
    var createdAt by Medias.createdAt

    fun dto() = Media(advertisement.value, uri, createdAt)
}