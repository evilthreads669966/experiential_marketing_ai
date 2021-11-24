package com.limemedia.user.service

import com.limemedia.user.repository.persistence.UserRepository

interface UserService {
    val repo: UserRepository
}