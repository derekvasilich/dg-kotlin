package com.example.dgkotlin.data.remote.service

import com.example.dgkotlin.data.remote.request.LoginRequest
import com.example.dgkotlin.data.remote.response.LegacyResponse
import io.reactivex.Observable

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers

interface LegacyUserService {
    @POST("users/login")
    @Headers("Accept: application/json")
    fun login(
        @Body body: LoginRequest
    ): Observable<LegacyResponse>
}
