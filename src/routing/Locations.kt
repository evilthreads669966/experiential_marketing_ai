package com.limemedia

import io.ktor.locations.*

@Location("/")
class Login
@Location("/")
class LoginPost
@Location("/logout")
class Logout

@Location("/api/v1")
class Api{
    @Location("/vehicles")
    class Vehicles
    @Location("/vehicle/{id}")
    data class Vehicle(val id: String) {
        @Location("/locations")
        data class Locations(val vehicle: Api.Vehicle, val from: Long? = null, val to: Long? = null, val limit: Int? = null)
    }
}

@Location("/admin")
class Admin{
    @Location("/vehicles")
    class Vehicles{
        @Location("/{id}")
        data class Locations(val id: String)
    }
}

@Location("/user")
class User{
    @Location("/clients")
    class Clients
    @Location("/ads")
    class Ads{
        @Location("/edit/{id}")
        data class Edit(val id: Int, var name: String? = null, var client: String? = null, var description: String? = null, var start: Long? = null, var end: Long? = null)
    }
}

@Location("styles.css")
class StyleSheet