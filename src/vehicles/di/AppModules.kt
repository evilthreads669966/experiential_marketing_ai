package com.limemedia

import com.limemedia.networking.Geotab
import com.limemedia.networking.GeotabService
import com.limemedia.networking.GeotabServiceImpl
import com.limemedia.repository.Repository
import com.limemedia.repository.RepositoryImpl
import com.limemedia.service.Service
import com.limemedia.service.ServiceImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import javax.sql.DataSource

object AppModules{
    val dataAccessModule = module {
        single<Geotab> { Geotab("MY_DB", "youremail@gmail.com", "secret") }
        singleBy<GeotabService, GeotabServiceImpl>()
        singleBy<Repository, RepositoryImpl>()
        singleBy<Service, ServiceImpl>()
    }

    val dataSourceModule = module {
        single<HikariConfig> {
            HikariConfig().apply{
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:test"
                maximumPoolSize = 3
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
        }
        single<DataSource> { HikariDataSource(get()) }
    }
}