package com.limemedia

import com.limemedia.data.podo.Location
import com.limemedia.data.podo.Vehicle
import com.limemedia.repository.persistence.db.tables.Locations
import com.limemedia.repository.persistence.db.tables.Vehicles
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SortOrder

// TODO: 6/5/21 put String instead of Int for EntityId and change IntIntent to Entity
class VehicleEntity(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<VehicleEntity>(Vehicles)
    var name by Vehicles.name
    var vehicleId by Vehicles.vehicleId
    var serial by Vehicles.serial
    var timeZoneId by Vehicles.timeZoneId
    var activeFrom by Vehicles.activeFrom
    var activeTo by Vehicles.activeTo
    //val locations by LocationEntity referrersOn(Locations.vehicleId)
}

// TODO: 6/5/21 put String instead of Int for EntityId and change IntIntent to Entity
class LocationEntity(id: EntityID<Int>): IntEntity(id){
    companion object: IntEntityClass<LocationEntity>(Locations)
    var latitude by Locations.latitude
    var longitude by Locations.longitude
    var date by Locations.date
    var locationId by Locations.locationId
    var vehicleId by Locations.vehicleId
    //if I do this then when I select from location entity it then also selects from the vehicles table after that.
    //var vehicle by VehicleEntity referencedOn Locations.vehicleId
}

fun VehicleEntity.toVehicle(): Vehicle = Vehicle(name, vehicleId.toUpperCase(), serial, activeFrom, activeTo, timeZoneId)

fun LocationEntity.toLocation(): Location = Location(date,latitude, longitude, locationId, vehicleId.toUpperCase() )

fun SizedIterable<LocationEntity>.orderDescending() = this.orderBy(Locations.date to SortOrder.DESC)
