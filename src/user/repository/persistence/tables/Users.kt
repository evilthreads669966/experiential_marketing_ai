package com.limemedia.user.repository.persistence.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column


abstract class StringIdTable(val idName: String, tablename: String): IdTable<String>(name = tablename) {
    override val id: Column<EntityID<String>> = varchar(name = idName, length = 30).entityId()
    override val primaryKey: PrimaryKey? by lazy { super.primaryKey ?: PrimaryKey(id, name = "pk_$idName") }
}

object Users: StringIdTable("username", "users"){
    val createdAt = long("created_at")
}

object Clients: StringIdTable("name","clients"){
    val username = reference("username", Users, fkName = "fk_username")
}

object Advertisements: IntIdTable("advertisement"){
    val client = reference("client", Clients)
    var name = varchar("name", 100).uniqueIndex()
    val description = varchar("description", 500)
    val createdAt = long("created_at")
    val startDate = long("start_date").nullable()
    val endDate = long("end_date").nullable()
    val impressions = integer("impressions").default(0)
    //val campaign = integer("campaign_id").references(Campaign.id).nullable()
}

object Medias: IntIdTable("media") {
    val advertisement = reference("advertisement", Advertisements)
    val uri = varchar("uri", 500).uniqueIndex()
    val createdAt = long("created_at")
}

/*object Campaign: IntIdTable("campaign"){
    val name = varchar("campaign", 25)
    val user = integer("client_id").references(User.id)
    val description = varchar("description", 500)
    val createdAt = long("created_at")
    val startDate = long("start_date").nullable()
    val endDate = long("end_date").nullable()
}*/