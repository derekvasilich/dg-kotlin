package com.example.dgkotlin.data.model

import java.util.Date

data class Visit (
    var routeId: Long,
    var quoteId: Long,
    var userId: Long
) {
    var id: Long? = null
    var visitedAt: Date? = null
}