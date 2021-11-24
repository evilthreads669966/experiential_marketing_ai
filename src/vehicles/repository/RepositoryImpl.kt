package com.limemedia.repository

import com.geotab.http.exception.InvalidUserException
import com.geotab.http.exception.JsonRpcErrorDataException
import com.geotab.http.exception.OverLimitException
import com.limemedia.LocationEntity
import com.limemedia.VehicleEntity
import com.limemedia.data.podo.Location
import com.limemedia.networking.GeotabService
import com.limemedia.orderDescending
import com.limemedia.repository.persistence.db.tables.Locations
import com.limemedia.repository.persistence.db.tables.Vehicles
import com.limemedia.toLocation
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.SocketException
import java.sql.SQLException
import java.time.LocalDateTime

class RepositoryImpl(private val service: GeotabService) : Repository {

    @Throws(InvalidUserException::class, JsonRpcErrorDataException::class, OverLimitException::class, IllegalArgumentException::class)
    override suspend fun batchInsertVehicles() {
        val vehicles = service.requestVehicles()
        transaction {
            vehicles.forEach { vehicle ->
                try{
                    VehicleEntity.new {
                        this.vehicleId = vehicle.vehicleId
                        this.serial = vehicle.serial
                        this.activeFrom = vehicle.activeFrom
                        this.activeTo = vehicle.activeTo
                        this.timeZoneId = vehicle.timeZoneId
                        this.name = vehicle.name
                    }
                }
                catch (e: SQLException){}
            }
        }
    }

    @Throws(InvalidUserException::class, JsonRpcErrorDataException::class, OverLimitException::class, IllegalArgumentException::class, NoSuchElementException::class, SocketException::class)
    override suspend fun batchInsertLocations(from: LocalDateTime) {
        val vehicles = selectAllVehicles()
        transaction {
            vehicles.forEach { vehicle ->
                try{
                    val locations = service.requestLocations(vehicle.vehicleId, from = from)
                    locations.forEach { location ->
                        try{
                            transaction{
                                val loc = Locations.select{ Locations.locationId eq location.locationId }.firstOrNull()
                                if(loc == null)
                                    LocationEntity.new {
                                        this.longitude = location.longitude
                                        this.latitude = location.latitude
                                        this.locationId = location.locationId
                                        this.vehicleId = vehicle.vehicleId
                                        this.date = location.epochTime
                                    }
                            }
                        }catch (e: Exception){}
                }
                }catch (e: Exception){}
            }
        }
    }

    override suspend fun selectLocations(vehicleId: String, from: Long?, to: Long?, resultLimit: Int?): List<Location> {
        val whereId = Op.build { Locations.vehicleId eq vehicleId }
        return transaction {
            val whereClause = when {
                (to != null && from != null) -> Op.build{ whereId and (Locations.date lessEq to) and (Locations.date greaterEq from) }
                (to == null && from != null) -> Op.build { whereId and (Locations.date greaterEq from) }
                (to != null && from == null) -> Op.build { whereId and (Locations.date lessEq to) }
                else -> Op.build { whereId }
            }
            return@transaction LocationEntity.run {
                if(resultLimit == null)
                    find { whereClause }
                else
                    find { whereClause }.limit(resultLimit)
            }.orderDescending().map { it.toLocation() }
        }
    }

    override suspend fun selectAllVehicles(): List<VehicleEntity> = newSuspendedTransaction(Dispatchers.IO) { VehicleEntity.all().toList() }

    override suspend fun selectVehicle(id: String): VehicleEntity? =
        newSuspendedTransaction(Dispatchers.IO) { VehicleEntity.find { Vehicles.vehicleId eq id }.firstOrNull() }
}