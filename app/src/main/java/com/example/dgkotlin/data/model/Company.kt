package com.example.dgkotlin.data.model

data class Company (
    var id: Long? = null,
    var type: String? = null,
    var companyName: String? = null,

    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,

    var phone: String? = null,
    var address_1: String? = null,
    var city: String? = null,
    var province: String? = null,
    var postal: String? = null,
    var country: String? = null,

    var lat: Float? = null,
    var lng: Float? = null
)
