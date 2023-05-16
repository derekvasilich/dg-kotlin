package com.example.dgkotlin.ui.routes

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dgkotlin.data.model.Route
import com.example.dgkotlin.data.remote.service.RouteService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RoutesViewModel(
    private val routeService: RouteService,
    private val context: Context
) : ViewModel() {

    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>> = _routes

    @SuppressLint("CheckResult")
    fun fetchRoutes() {
        routeService.findAllPaginated(1, 10, "id,DESC")
            .map{ res -> res.content }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list -> _routes.value = list },
                { error -> Toast.makeText(context, "Error loading routes: ${error.message}", Toast.LENGTH_SHORT).show(); }
            )
    }

}