package com.sevalk.data.models

data class ServiceLocation(
    val address: String = "",
    val city: String = "",
    val province: String = "",
    val country: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
