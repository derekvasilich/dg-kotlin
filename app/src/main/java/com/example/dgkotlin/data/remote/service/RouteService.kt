package com.example.dgkotlin.data.remote.service

import com.example.dgkotlin.data.model.PagedList
import com.example.dgkotlin.data.model.Route
import com.example.dgkotlin.data.model.Visit
import io.reactivex.Observable

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

public interface RouteService {
    @GET("routes")
    fun findAll(): Observable<Array<Route>>

    @GET("routes/paginated")
    fun findAllPaginated(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Observable<PagedList<Route>>

    @GET("routes/paginated")
    fun findAllPaginated(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Observable<PagedList<Route>>

    @GET("routes/{id}")
    fun findById(
        @Path("id") id: Long
    ): Observable<Route>

    @POST("routes/visit")
    fun saveRouteLocationVisit(
        @Body body: Visit
    ): Observable<Visit>
}
