package com.example.dg_andriod.data.remote

import android.content.Context
import com.example.dgkotlin.data.remote.RetrofitClient
import com.example.dgkotlin.data.remote.service.CustomerService
import com.example.dgkotlin.data.remote.service.LegacyUserService
import com.example.dgkotlin.data.remote.service.RouteService
import com.example.dgkotlin.data.remote.service.UserService

class ApiUtils() {

    companion object {
        fun getLegacyUserService(context: Context): LegacyUserService {
            return RetrofitClient(context)
                .retrofitLegacy
                .create(LegacyUserService::class.java)
        }

        fun getUserService(context: Context): UserService {
            return RetrofitClient(context)
                .retrofit
                .create(UserService::class.java)
        }

        fun getCustomerService(context: Context): CustomerService {
            return RetrofitClient(context)
                .retrofit
                .create(CustomerService::class.java)
        }

        fun getRouteService(context: Context): RouteService {
            return RetrofitClient(context)
                .retrofit
                .create(RouteService::class.java)
        }

    }
}