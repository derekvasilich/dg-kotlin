package com.example.dgkotlin.data.remote

import android.content.Context
import com.example.dgkotlin.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient (context: Context ) {
    private var _context: Context = context

    private var loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.HEADERS)

    private var _httpAuthClient: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        var newRequest: Request = chain.request()
        if (newRequest.header("No-Authentication") == null) {
            val token: String? = _context.getSharedPreferences(
                BuildConfig.APPLICATION_ID,
                Context.MODE_PRIVATE
            ).getString(BuildConfig.JWT_TOKEN_PREF_KEY, null)
            if (token != null) {
                newRequest = newRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }
        }
        chain.proceed(newRequest)
    }.addInterceptor(loggingInterceptor).build();

    var retrofit: Retrofit = Retrofit.Builder()
        .client(_httpAuthClient)
        .baseUrl(BuildConfig.API_BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var retrofitLegacy: Retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
        .baseUrl(BuildConfig.LEGACY_API_BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}