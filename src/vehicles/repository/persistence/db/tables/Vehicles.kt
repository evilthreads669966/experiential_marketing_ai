package com.limemedia.repository.persistence.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

// TODO: 6/5/21 change to IdTable and make vehicleId the primary key instead
object Vehicles: IntIdTable("vehicles"){
    val name = varchar("name", 50)
    val vehicleId = varchar("vehicle_id", 5).uniqueIndex()
    val serial = varchar("serial", 20)
    val timeZoneId = varchar("timeZoneId", 25)
    val activeFrom = long("active_from")
    val activeTo = long("active_to")
}