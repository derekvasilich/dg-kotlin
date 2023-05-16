package com.example.dgkotlin.ui.routes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.dgkotlin.BuildConfig
import com.example.dgkotlin.R
import com.example.dgkotlin.data.model.Route
import com.example.dgkotlin.databinding.FragmentRoutesBinding

class RoutesFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var routesViewModel: RoutesViewModel
    private var _binding: FragmentRoutesBinding? = null
    private var listView: ListView ?= null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        routesViewModel = ViewModelProvider(this, RoutesViewModelFactory(requireContext()))[RoutesViewModel::class.java]

        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listView = binding.routesList
        listView!!.onItemClickListener = this

        val prefs = requireActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        val currentRouteIdx = prefs.getInt("currentRouteIndex", -1)

        routesViewModel.routes.observe( viewLifecycleOwner, Observer {
            val routes = it ?: return@Observer
            listView!!.adapter = RouteFieldAdapter(requireContext(), routes)
            listView!!.setSelection(currentRouteIdx)
        })
        routesViewModel.fetchRoutes()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
        val route: Route? = routesViewModel.routes.value?.get(i)
        if (route != null) {
            listView!!.setSelection(i)
            val prefs = requireActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
            with (prefs.edit()) {
                putInt("currentRouteIndex", i)
                putLong("routeId", route.id ?: -1)
            }.apply()
            val navController: NavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.nav_map)
        }
    }
}