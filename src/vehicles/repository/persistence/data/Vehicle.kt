package com.limemedia.data.podo

import com.geotab.model.entity.device.Device
import java.time.ZoneOffset

data class Vehicle(
    val name: String,
    val vehicleId: String,
    val serial: String,
    val activeFrom: Long,
    val activeTo: Long,
    val timeZoneId: String,
)

fun Device.toVehicle(): Vehicle? = this.takeIf { it.id?.id != null }?.let {
    return Vehicle(name, id.id, serialNumber, activeFrom.toEpochSecond(ZoneOffset.UTC), activeTo.toEpochSecond(ZoneOffset.UTC), timeZoneId)
}