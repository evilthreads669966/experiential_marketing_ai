package com.limemedia.repository.persistence.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

// TODO: 6/5/21 change to IdTable and make primary key the locationId
object Locations: IntIdTable("locations"){
    val latitude = double("latitude")
    val longitude = double("longitude")
    val date = long("date")
    val locationId = varchar("location_id", 25).uniqueIndex()
    val vehicleId = reference("vehicle_id", Vehicles.vehicleId)
}