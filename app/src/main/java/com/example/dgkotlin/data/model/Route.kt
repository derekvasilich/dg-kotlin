package com.example.dgkotlin.data.model

import java.util.Date

class Route {

    var id: Long? = null
    var company: Company? = null
    var date: Date? = null
    var truck: String? = null
    var startAddress: Int? = null
    var quoteIds: String? = null
    var quotes: List<Quote>? = null
    var duration: Int? = null
    var distance: Int? = null
    var emailed: Boolean? = null

    companion object {
        var GEOFENCE_RADIUS_IN_METRES: Long = 250L
        var GEOFENCE_EXPIRATION_DURATION_MS: Long = 43200000
        var GEOFENCE_LOITERING_DELAY_MS: Long = 300L // 5m

        var startPointList: List<StartPoint> = listOf(
            StartPoint.create("ABA - Allison Brothers Asphalt", "30 Adelaide St N, London, ON",  42.9770286,  -81.22500689999998),
            StartPoint.create("Cambridge Hotel and Conference Centre", "700 Hespeler Rd, Cambridge, ON", 43.4095403,  -80.3292753 ),
            StartPoint.create("Residence & Conference Centre - Barrie", "101 Georgian Dr, Barrie, ON", 44.4133088, -79.66556149999997),
            StartPoint.create("London Asphalt Plant", "1788 Clark Rd, London, ON", 43.0500484, -81.1978533),
            StartPoint.create("Woodstock Asphalt Plant", "594728 Oxford 59, Woodstock, ON", 43.101577, -80.7303398)
        )
    }

    class StartPoint(
        var name: String,
        var address: String,
        var lat: Double,
        var lng: Double
    ) {
        companion object {
            fun create(name: String, address: String, lat: Double, lng: Double) = StartPoint(name, address, lat, lng)
        }
    }

}