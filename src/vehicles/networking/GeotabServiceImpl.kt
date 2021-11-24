package com.limemedia.networking

import com.geotab.http.exception.InvalidUserException
import com.geotab.http.exception.JsonRpcErrorDataException
import com.geotab.http.exception.OverLimitException
import com.geotab.http.request.AuthenticatedRequest
import com.geotab.http.request.param.AuthenticatedParameters
import com.geotab.http.request.param.SearchParameters
import com.geotab.http.response.DeviceListResponse
import com.geotab.http.response.LogRecordListResponse
import com.geotab.model.Id
import com.geotab.model.entity.device.Device
import com.geotab.model.entity.logrecord.LogRecord
import com.geotab.model.search.LogRecordSearch
import com.geotab.model.search.Search
import com.geotab.model.serialization.DateTimeSerializationUtil
import com.limemedia.data.podo.Location
import com.limemedia.data.podo.Vehicle
import com.limemedia.data.podo.toLocation
import com.limemedia.data.podo.toVehicle
import java.net.SocketException
import java.time.LocalDateTime
import java.util.*

class GeotabServiceImpl(private val geotab: Geotab) : GeotabService {

    @Throws(InvalidUserException::class, JsonRpcErrorDataException::class, OverLimitException::class, IllegalArgumentException::class)
    override fun requestVehicles() : List<Vehicle>{
        val request: AuthenticatedRequest<*> = AuthenticatedRequest.authRequestBuilder<AuthenticatedParameters>().apply {
            method("Get")
            val args = SearchParameters.searchParamsBuilder<Search>().apply {
                credentials(geotab.credentials)
                typeName("Device")
            }.build()
            params(args)
        }.build()
        val result: Optional<List<Device>> = geotab.api.call(request, DeviceListResponse::class.java)
        return result.get().map { it.toVehicle() }.filterNotNull()
    }

    @Throws(InvalidUserException::class, JsonRpcErrorDataException::class, OverLimitException::class, IllegalArgumentException::class, NoSuchElementException::class, SocketException::class)
    override fun requestLocations(id: String, from: LocalDateTime, limit: Int?): List<Location>{
        //form date range arguments
        val to = DateTimeSerializationUtil.nowUtcLocalDateTime()
        //create LogRecordSearch from date range and vehicle ID
        val logRecordSearch = LogRecordSearch( from, to, Id(id))
        //create SearchParameters  with LogRecordSearch to pass into as a paremeter to Request
        val args = SearchParameters<LogRecordSearch>().apply {
            search = logRecordSearch
            credentials = geotab.credentials
            typeName = "LogRecord"
            limit?.let {  resultsLimit = limit }
        }
        //create http get request object with SearchParameters
        val request: AuthenticatedRequest<AuthenticatedParameters> = AuthenticatedRequest.authRequestBuilder<AuthenticatedParameters>().apply {
            method("Get")
            params(args)
        }.build()
        //make call to server with request
        val result: Optional<List<LogRecord>> = geotab.api.call(request, LogRecordListResponse::class.java)
        try {
            return result.get().map { it.toLocation() }.filterNotNull()
        }catch (e: NoSuchElementException){
            return emptyList()
        }
    }
}