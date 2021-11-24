package com.limemedia

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.limemedia.data.podo.Location
import com.limemedia.data.podo.Vehicle
import io.kotest.assertions.ktor.shouldHaveContentType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.longs.shouldBeLessThanOrEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldBeEqualIgnoringCase
import io.kotest.matchers.string.shouldNotBeBlank
import io.ktor.http.*
import io.ktor.server.testing.*
import java.time.Instant

class ApiTest: FunSpec({
    lateinit var gson: Gson
    val valentines: Long = 1613345878
    val newYearsDay: Long = 1609462800
    val vehicleId = "b36"

    beforeTest {
        gson = GsonBuilder().setPrettyPrinting().create()
    }

    test("api vehicle endpoint produces json vehicles.repository.persistence.data"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicles").run {
                response.shouldHaveContentType(ContentType.parse("application/json; charset=UTF-8"))
            }
        }
    }

    test("vehicles endpoint has vehicles"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicles").run {
                val vehicles : List<Vehicle> = gson.fromJson(response.content, object : TypeToken<List<Vehicle>>() {}.type)
                vehicles.shouldNotBeEmpty()
            }
        }
    }

    test("vehicle endpoint has a vehicle for the provided ID"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicle/$vehicleId").run {
                val vehicle : Vehicle = gson.fromJson(response.content, object : TypeToken<Vehicle>() {}.type)
                vehicle.vehicleId.shouldBeEqualIgnoringCase(vehicleId)
                vehicle.name.shouldNotBeNull()
                vehicle.name.shouldNotBeBlank()
                vehicle.serial.shouldNotBeNull()
                vehicle.serial.shouldNotBeBlank()
                vehicle.activeFrom.shouldBeGreaterThanOrEqual(Instant.EPOCH.toEpochMilli())
                vehicle.activeFrom.shouldBeLessThan(vehicle.activeTo)
                vehicle.activeTo.shouldBeGreaterThan(vehicle.activeFrom)
                vehicle.activeTo.shouldBeLessThanOrEqual(System.currentTimeMillis())
                vehicle.activeTo.shouldBeGreaterThanOrEqual(Instant.EPOCH.toEpochMilli())
            }
        }
    }

    test("vehicle locations with optional limit parameter set to 1000 only returns 1000 locations"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicle/$vehicleId/locations?limit=1000").run {
                val locationHistory : List<Location> = gson.fromJson(response.content, object : TypeToken<List<Location>>() {}.type)
                locationHistory.shouldHaveSize(1000)
            }
        }
    }



    test("vehicle location history before valentines day"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicle/$vehicleId/locations?to=$valentines").run {
                val locationHistory : List<Location> = gson.fromJson(response.content, object : TypeToken<List<Location>>() {}.type)
                locationHistory.shouldNotBeEmpty()
                locationHistory.forEach {
                    it.epochTime.shouldBeLessThanOrEqual(valentines)
                }
            }
        }
    }

    test("vehicle location history after valentines day"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicle/$vehicleId/locations?from=$valentines").run {
                val locationHistory : List<Location> = gson.fromJson(response.content, object : TypeToken<List<Location>>() {}.type)
                locationHistory.shouldNotBeEmpty()
                locationHistory.forEach {
                    it.epochTime.shouldBeGreaterThanOrEqual(valentines)
                }
            }
        }
    }

    test("vehicle location history after new years eve and before valentines day"){
        withTestApplication({ module(testing = true)}){
            handleRequest(HttpMethod.Get, "/api/v1/vehicle/$vehicleId/locations?from=$newYearsDay&to=$valentines").run {
                val locationHistory : List<Location> = gson.fromJson(response.content, object : TypeToken<List<Location>>() {}.type)
                locationHistory.shouldNotBeEmpty()
                locationHistory.forEach {
                    it.epochTime.shouldBeLessThanOrEqual(valentines)
                    it.epochTime.shouldBeGreaterThanOrEqual(newYearsDay)
                }
            }
        }
    }
})