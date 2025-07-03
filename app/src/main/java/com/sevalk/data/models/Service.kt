package com.sevalk.data.models

data class Service(
    val id: Int,
    val name: String,
    val description: String,
    val pricingModel: PricingModel,
    val isSelected: Boolean = false,
    val price: String = "",
    val isExpanded: Boolean = false
)
