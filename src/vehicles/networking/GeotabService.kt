package com.limemedia.networking

import com.limemedia.data.podo.Location
import com.limemedia.data.podo.Vehicle
import java.time.LocalDateTime

interface GeotabService {
    fun requestVehicles(): List<Vehicle>

    fun requestLocations(id: String, from: LocalDateTime, limit: Int? = null): List<Location>
}