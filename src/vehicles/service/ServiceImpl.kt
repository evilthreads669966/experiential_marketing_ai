package com.limemedia.service

import com.geotab.http.exception.InvalidUserException
import com.geotab.http.exception.JsonRpcErrorDataException
import com.geotab.http.exception.OverLimitException
import com.geotab.model.serialization.DateTimeSerializationUtil
import com.limemedia.data.podo.Location
import com.limemedia.data.podo.Vehicle
import com.limemedia.repository.Repository
import com.limemedia.toVehicle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.ticker
import java.net.SocketException
import java.sql.SQLException
import java.time.LocalDateTime
import kotlin.system.exitProcess

class ServiceImpl(private val repo: Repository) : Service {

    override suspend fun findVehicle(id: String): Vehicle? = repo.selectVehicle(id.toLowerCase())?.toVehicle()

    override suspend fun findVehicles(): List<Vehicle> = repo.selectAllVehicles().map { it.toVehicle() }

    override suspend fun findLocations(vehicleId: String, from: Long?, to: Long?, limit: Int?): List<Location> {
        return repo.selectLocations(vehicleId.toLowerCase(), from, to, limit)
    }

    override suspend fun addAllVehiclesAndLocations(scope: CoroutineScope) {
        var initial = true
        ticker(1000 * 60 * 30, 0, scope.coroutineContext).consumeEach {
            if(initial){
                val from = DateTimeSerializationUtil.nowUtcLocalDateTime().minusMinutes(180)
                populateDatabaseTablesFromGeotab(from)
                initial = false
            }else{
                val from = DateTimeSerializationUtil.nowUtcLocalDateTime().minusMinutes(30)
                populateDatabaseTablesFromGeotab(from)
            }
        }
    }

    private suspend fun populateDatabaseTablesFromGeotab(fromTime: LocalDateTime){
        try{
            val vehicles = repo.selectAllVehicles()
            if(vehicles.isEmpty())
                repo.batchInsertVehicles()
        }catch(e: InvalidUserException){
            System.out.println("Invalid Geotab credentials. Unable to authenticate!")
            exitProcess(1)
        }catch (e: JsonRpcErrorDataException){
            System.out.println("Geotab API call limit exceeded. Too many requests!!")
        }catch(e: OverLimitException){
            System.out.println("API call limit exceeded. Unable to make requests!!")
        }catch (e: IllegalArgumentException){
            System.out.println("call was missing request or there is no response type declared for the result")
        }catch (e: SQLException){
            System.out.println("Failed to insert vehicles into database!")
        }catch (e: SocketException){
            System.out.println("Connection timed out")
        }
        try{
            repo.batchInsertLocations(fromTime)
        }catch(e: InvalidUserException){
            System.out.println("Invalid Geotab credentials. Unable to authenticate!")
            exitProcess(1)
        }catch (e: JsonRpcErrorDataException){
            System.out.println("Geotab API call limit exceeded. Too many requests!!")
        }catch(e: OverLimitException){
            System.out.println("API call limit exceeded. Unable to make requests!!")
        }catch (e: IllegalArgumentException){
            System.out.println("call was missing request or there is no response type declared for the result")
        }catch (e: SQLException){
            System.out.println("Failed to insert vehicles into database!")
        }catch (e: SocketException){
            System.out.println("Connection timed out")
        }
    }
}