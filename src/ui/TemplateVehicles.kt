package com.limemedia.ui

import com.limemedia.data.podo.Vehicle
import kotlinx.html.*
import java.time.LocalDateTime
import java.time.ZoneOffset

fun FlowContent.showVehicles(vehicles: List<Vehicle>){
    h1 { + "Vehicles" }
    table(classes = "center") {
        thead {
            tr {
                td { +"Vehicles" }
            }
        }
        tr {
            th { + "Name" }
            th { + "ID" }
            th { + "Serial Number" }
            th { + "Time Zone ID" }
            th { + "Active From" }
            th { + "Active To" }
        }
        vehicles.forEach {  vehicle ->
            tr {
                td { a(href = "/admin/vehicles/${vehicle.vehicleId.toLowerCase()}") { + vehicle.name } }
                td { + vehicle.vehicleId }
                td { + vehicle.serial }
                td { + vehicle.timeZoneId }
                td { + LocalDateTime.ofEpochSecond(vehicle.activeFrom, 0, ZoneOffset.UTC).toString() }
                td { +  LocalDateTime.ofEpochSecond(vehicle.activeTo, 0, ZoneOffset.UTC).toString() }
            }
        }
    }
}