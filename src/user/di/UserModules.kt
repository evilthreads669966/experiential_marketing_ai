package com.limemedia.user.di

import com.limemedia.user.networking.AgilityService
import com.limemedia.user.networking.AgilityServiceImpl
import com.limemedia.user.repository.persistence.UserRepository
import com.limemedia.user.repository.persistence.UserRepositoryImpl
import com.limemedia.user.service.UserService
import com.limemedia.user.service.UserServiceImpl
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy

object UserModules {
    val dataAccessModule = module {
        singleBy<UserRepository, UserRepositoryImpl>()
        singleBy<UserService, UserServiceImpl>()
        singleBy<AgilityService, AgilityServiceImpl>()
    }
}