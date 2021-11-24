package com.limemedia.user.repository.persistence.data

data class Advertisement(val id: Int, val clientId: String, val name: String, val description: String, val createdAt: Long, val startDate: Long?, val endDate: Long?, val impressions: Int){
    companion object{
        const val UPLOADS_DIR = ".uploads"
    }
}