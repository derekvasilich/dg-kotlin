package com.example.dgkotlin.data.model

class PagedList<T> {
    var page: Int = 0
    var content: List<T> = ArrayList()
        set(results) {
            field = content
        }
    var totalResults: Int = 0
    var totalPages: Int = 0
    var last: Boolean = false
    var numberOfElements: Int = 0
    var sort: Any? = null
    var pageable: Any? = null
    var number: Int = 0
    var first: Boolean = false
    var size: Int = 0
    var empty: Boolean = false
}