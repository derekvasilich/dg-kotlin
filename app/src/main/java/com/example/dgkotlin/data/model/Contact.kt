package com.example.dgkotlin.data.model

class Contact {
    var id: Long? = null
    var phone: String? = null
    var mobile: String? = null
    var address_1: String? = null
    var city: String? = null
    var province: String? = null
    var postal: String? = null
    var country: String? = null
    var lat: Double? = null
    var lng: Double? = null

    val address: String
        get() = "$address_1, $city, $province, $postal"
}