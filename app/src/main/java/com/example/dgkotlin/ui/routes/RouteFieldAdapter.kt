package com.example.dgkotlin.ui.routes

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.dgkotlin.data.model.Route
import com.example.dgkotlin.databinding.RouteRowBinding

class RouteFieldAdapter(
    private val context: Context,
    private val routes: List<Route>
) : ArrayAdapter<Route>(context, -1, routes) {

    private var _binding: RouteRowBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        _binding = RouteRowBinding.inflate(inflater, parent, false)

        val root: View = binding.root

        val route = routes[position]

        binding.routeDate.text = route.date.toString()
        binding.routeCompany.text = route.company?.companyName
        binding.routeAddress.text = route.quotes?.size.toString() + " jobs"

        return root
    }

}