package com.limemedia.data.podo

import com.geotab.model.entity.logrecord.LogRecord
import java.time.ZoneOffset

data class Location(
    val epochTime: Long,
    val latitude: Double,
    val longitude: Double,
    val locationId: String,
    val vehicleId: String,
)

fun LogRecord.toLocation(): Location? = this.takeIf { it.device?.id?.id != null && it.id?.id != null }?.let {
    return Location(this.dateTime.toEpochSecond(ZoneOffset.UTC), this.latitude, this.longitude, this.id?.id!!, this.device.id?.id!! ?: "")
}
