package com.example.dgkotlin.data.remote.service

import com.example.dgkotlin.data.model.Customer
import io.reactivex.Observable
import retrofit2.http.GET

interface CustomerService {
    @GET("customers")
    fun findAll(): Observable<List<Customer>>
}