package com.limemedia.user.service

import com.limemedia.user.repository.persistence.UserRepository

class UserServiceImpl(override val repo: UserRepository) : UserService {


}