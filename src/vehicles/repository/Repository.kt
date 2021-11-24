package com.limemedia.repository

import com.limemedia.VehicleEntity
import com.limemedia.data.podo.Location
import java.time.LocalDateTime

interface Repository {
    suspend fun batchInsertVehicles()

    suspend fun batchInsertLocations(from: LocalDateTime)

    suspend fun selectLocations(vehicleId: String, from: Long? = null, to: Long? = null, resultLimit: Int? = null): List<Location>

    suspend fun selectAllVehicles(): List<VehicleEntity>

    suspend fun selectVehicle(id: String): VehicleEntity?
}