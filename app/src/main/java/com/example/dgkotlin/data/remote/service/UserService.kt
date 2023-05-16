package com.example.dgkotlin.data.remote.service

import com.example.dgkotlin.data.model.User
import com.example.dgkotlin.data.remote.request.LoginRequest
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Headers

interface UserService {
    @POST("login")
    @Headers("No-Authentication: true")
    fun login(
        @Body body: LoginRequest
    ): Observable<User>
}