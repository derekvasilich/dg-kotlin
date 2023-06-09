package com.example.dgkotlin.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dgkotlin.data.model.Customer
import com.example.dgkotlin.data.model.Route
import com.example.dgkotlin.data.model.Visit
import com.example.dgkotlin.data.remote.service.CustomerService
import com.example.dgkotlin.data.remote.service.RouteService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MapViewModel(
    private var context: Context,
    private var customerService: CustomerService,
    private var routeService: RouteService
) : ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    private val _customers = MutableLiveData<List<Customer>>()
    val customers: LiveData<List<Customer>> = _customers

    private val _visited = MutableLiveData<MutableList<Long>>()
    val visited: LiveData<MutableList<Long>> = _visited

    @SuppressLint("CheckResult")
    fun fetchRouteById(routeId: Long) {
        routeService.findById(routeId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { route -> _route.value = route },
                { error -> Toast.makeText(context, "Error loading route $routeId: ${error.message}", Toast.LENGTH_SHORT).show(); }
            )
    }

    @SuppressLint("CheckResult")
    fun fetchCustomers() {
        customerService.findAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { customers -> _customers.value = customers },
                { error -> Toast.makeText(context, "Error loading route customers: ${error.message}", Toast.LENGTH_SHORT).show(); }
            )
    }

    @SuppressLint("CheckResult")
    fun saveRouteLocationVisit(userId: Long, routeId: Long, quoteId: Long) {
        val visitRequest = HashMap<String, Any>()
        visitRequest["userId"] = userId
        visitRequest["routeId"] = routeId
        visitRequest["quoteId"] = quoteId
        if (_visited.value == null) {
            _visited.value = mutableListOf()
        }
        routeService.saveRouteLocationVisit(Visit(routeId, quoteId, userId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { res -> _visited.value?.add(quoteId) },
                { error ->
                    _visited.value?.add(quoteId)
                    Toast.makeText(context, "Error saving visit: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
    }
}