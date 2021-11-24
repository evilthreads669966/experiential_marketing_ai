package com.limemedia.service

import com.limemedia.data.podo.Location
import com.limemedia.data.podo.Vehicle
import kotlinx.coroutines.CoroutineScope

interface Service {
    suspend fun findVehicle(id: String): Vehicle?

    suspend fun findVehicles(): List<Vehicle>

    suspend fun findLocations(vehicleId: String, from: Long? = null, to: Long? = null, limit: Int? = null): List<Location>

    suspend fun addAllVehiclesAndLocations(scope: CoroutineScope)
}