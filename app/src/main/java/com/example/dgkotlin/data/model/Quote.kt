package com.example.dgkotlin.data.model

data class Quote (
    var id: Long? = null,
    var customer: Customer? = null,
    var contact: Contact? = null,
    var visits: List<Visit> = listOf()
)