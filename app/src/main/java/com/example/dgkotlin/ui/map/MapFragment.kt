package com.example.dgkotlin.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.dgkotlin.BuildConfig
import com.example.dgkotlin.BuildConfig.USER_ID_PREF_KEY
import com.example.dgkotlin.R
import com.example.dgkotlin.data.model.Contact
import com.example.dgkotlin.data.model.Customer
import com.example.dgkotlin.data.model.Quote
import com.example.dgkotlin.data.model.Route
import com.example.dgkotlin.databinding.FragmentMapBinding
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.function.Consumer
import java.util.stream.Collectors

class MapFragment : Fragment(), OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, View.OnClickListener  {

    companion object {
        var ICON_START = BitmapDescriptorFactory.HUE_CYAN
        var ICON_LOCATION = BitmapDescriptorFactory.HUE_RED
        var ICON_VISITED = BitmapDescriptorFactory.HUE_GREEN
        var REQUEST_CODE_PERMISSIONS = 1001
        var REQUIRED_PERMISSIONS = arrayOf(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
        )
    }

    private lateinit var _mapViewModel: MapViewModel
    private var _binding: FragmentMapBinding? = null

    private val quoteList: MutableList<Quote> = ArrayList()
    private val markerMap: MutableMap<Long, Marker> = HashMap()

    private var currentQuote: Quote? = null
    private var selectedQuote: Quote? = null
    private lateinit var viewJobButton: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _mapViewModel = ViewModelProvider(this, MapViewModelFactory(requireContext()))[MapViewModel::class.java]
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewJobButton = binding.viewJobButton
        viewJobButton.setOnClickListener(this)

        if (allPermissionsGranted()) {
            startMap() // start map if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        return root
    }

    private fun allPermissionsGranted(): Boolean {
        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun startMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.frag_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(readyMap: GoogleMap) {
        googleMap = readyMap
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMarkerClickListener(this)

        val locationRequest: LocationRequest = LocationRequest.Builder(Route.GEOFENCE_LOITERING_DELAY_MS).build()
        val locationSettingsRequest: LocationSettingsRequest = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        settingsClient.checkLocationSettings(locationSettingsRequest)

        LocationServices.getFusedLocationProviderClient(requireContext()).requestLocationUpdates(locationRequest, this, Looper.myLooper())

        _mapViewModel.route.observe( viewLifecycleOwner, Observer {
            val route  = it ?: return@Observer
            drawRoute(route)
        })
        _mapViewModel.customers.observe( viewLifecycleOwner, Observer {
            val customers  = it ?: return@Observer
            drawCustomers(customers)
        })

        val routeId = requireActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).getLong("routeId", -1)
        if (routeId > -1) {
            _mapViewModel.fetchRouteById(routeId)
        } else {
            _mapViewModel.fetchCustomers()
        }

        _mapViewModel.visited.observe(viewLifecycleOwner, Observer {
            val visited = it ?: return@Observer
            drawVisitedQuotes(visited)
        })
    }

    private fun drawVisitedQuotes(visited: List<Long>) {
        visited.stream().forEach { quoteId ->
            val quote = quoteList.stream().filter { (id): Quote -> id === quoteId }.findFirst().orElse(null)
            if (quote != null) {
                val quoteMarker = markerMap[quoteId]
                if (quoteMarker != null) {
                    val visitedMarker = MarkerOptions()
                        .position(quoteMarker.position)
                        .title(quote.customer!!.name)
                        .snippet(quote.contact!!.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(ICON_VISITED))
                    quoteMarker.remove()
                    val marker: Marker? = googleMap.addMarker(visitedMarker)
                    if (marker != null) {
                        marker.tag = quote.id
                        markerMap[quote.id!!] = marker
                    }
                }
            }
        }
    }

    private fun drawRoute(route: Route) {
        val startPoint: Route.StartPoint = Route.startPointList[route.startAddress!!]
        val startLatLng = LatLng(startPoint.lat, startPoint.lng)
        val startMarkerOpts = MarkerOptions()
            .position(startLatLng)
            .title(startPoint.address)
            .icon(BitmapDescriptorFactory.defaultMarker(ICON_START))
        val startMarker = googleMap.addMarker(startMarkerOpts)
        if (startMarker != null) markerMap[0L] = startMarker
        val bounds = LatLngBounds.Builder()
        bounds.include(startLatLng)
        if (route.quotes!!.isNotEmpty()) {
            route.quotes!!.forEach { quote ->
                val contact: Contact = quote.contact!!
                val customer: Customer = quote.customer!!
                if (contact.lat != null && contact.lng != null) {
                    val point = LatLng(contact.lat!!, contact.lng!!)
                    val quoteMarkerOpts = MarkerOptions()
                        .position(point)
                        .title(customer.name)
                        .snippet(contact.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(ICON_LOCATION))
                    val quoteMarker = googleMap.addMarker(quoteMarkerOpts)
                    if (quoteMarker != null) {
                        quoteMarker.tag = quote.id
                        markerMap[quote.id!!] = quoteMarker
                    }
                    bounds.include(point)
                    quoteList.add(quote)
                }
            }
        }
        val padding = 40
        val cu = CameraUpdateFactory.newLatLngBounds(bounds.build(), padding)
        googleMap.moveCamera(cu)
    }

    private fun drawCustomers(customerList: List<Customer?>) {
        if (customerList.isNotEmpty()) {
            customerList.forEach { customer ->
                if (customer!!.lat != null && customer.lng != null) {
                    val point = LatLng(customer.lat!!.toDouble(), customer.lng!!.toDouble())
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(point)
                            .title(customer.name)
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuoteControls() {
        if (currentQuote != null || selectedQuote != null) {
            val jobId = if (selectedQuote != null) selectedQuote!!.id else currentQuote?.id
            viewJobButton.text = "View Job $jobId"
            viewJobButton.visibility = View.VISIBLE
        } else {
            viewJobButton.visibility = View.INVISIBLE
        }
    }

    private fun intersectingQuotes(center: Location): List<Quote>? {
        return quoteList.stream().filter { quote ->
            val results = FloatArray(1)
            Location.distanceBetween(
                center.latitude,
                center.longitude,
                quote.contact!!.lat!!,
                quote.contact!!.lng!!,
                results
            )
            results[0] <= Route.GEOFENCE_RADIUS_IN_METRES
        }.collect(Collectors.toList())
    }

    override fun onLocationChanged(location: Location) {
        val intersections = intersectingQuotes(location)
        intersections!!.forEach(Consumer { quote: Quote ->
            currentQuote = quote
            if (_mapViewModel.visited.value?.contains(quote.id) != true) {
                Toast.makeText(
                    context,
                    "Arrived at: " + quote.contact!!.address,
                    Toast.LENGTH_SHORT
                ).show()
                val prefs = requireActivity().getSharedPreferences(
                    BuildConfig.APPLICATION_ID,
                    Context.MODE_PRIVATE
                )
                val routeId = prefs.getLong("routeId", -1)
                val userId = prefs.getLong(USER_ID_PREF_KEY, 0)
                _mapViewModel.saveRouteLocationVisit(userId, routeId!!, quote.id!!)
            }
        })
        if (intersections.isNotEmpty()) {
            currentQuote = null
        }
        updateQuoteControls()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val quoteId = marker.tag as Long?
        if (quoteId != null) {
            selectedQuote = quoteList.stream().filter { (id): Quote -> id === quoteId }.findFirst().orElse(null)
            updateQuoteControls()
        } else {
            selectedQuote = null
        }
        return false
    }

    override fun onClick(view: View?) {
        val navController = findNavController(requireActivity(), R.id.nav_host_fragment_content_main)
        val bundle = Bundle()
        bundle.putLong(
            "quoteId",
            (if (selectedQuote != null) selectedQuote!!.id else currentQuote!!.id)!!
        )
        navController.navigate(R.id.nav_webview, bundle)
    }
}