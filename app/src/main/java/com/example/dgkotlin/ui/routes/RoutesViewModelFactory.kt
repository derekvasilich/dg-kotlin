package com.example.dgkotlin.ui.routes

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dg_andriod.data.remote.ApiUtils

class RoutesViewModelFactory(val context: Context) : ViewModelProvider.Factory {

    private val _context: Context = context;

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutesViewModel::class.java)) {
            return RoutesViewModel(
                routeService = ApiUtils.getRouteService(_context),
                context = _context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}