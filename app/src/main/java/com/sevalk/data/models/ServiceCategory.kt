package com.sevalk.data.models

data class ServiceCategory(
    val name: String,
    val services: List<Service>,
    val isExpanded: Boolean = true
)
