package com.example.dgkotlin.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dg_andriod.data.remote.ApiUtils

class MapViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    private val _context: Context = context;

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                context = _context,
                customerService = ApiUtils.getCustomerService(_context),
                routeService = ApiUtils.getRouteService(_context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}