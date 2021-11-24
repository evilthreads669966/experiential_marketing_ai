package com.limemedia.ui

import com.limemedia.data.podo.Location
import kotlinx.html.*
import java.time.LocalDateTime
import java.time.ZoneOffset

fun FlowContent.showLocations(vehicleId: String, locations: List<Location>){
    h1 { + "Vehicle Records for ${vehicleId}" }
    table(classes = "center") {
        thead {
            tr {
                td { + "Record ID" }
                td { + "Date/Time" }
                td { + "Latitude" }
                td { + "Longitude" }
                // td { + "Speed" }
            }
        }
        locations.forEach { location ->
            tr {
                td { + location.locationId }
                td { + LocalDateTime.ofEpochSecond(location.epochTime, 0, ZoneOffset.UTC).toString()  }
                td { + "${location.latitude}" }
                td { + "${location.longitude}" }
                td { a(href = "https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}") { + "View on Google Map" } }
            }
        }
    }
}
