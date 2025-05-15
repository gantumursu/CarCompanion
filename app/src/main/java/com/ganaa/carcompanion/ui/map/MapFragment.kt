package com.ganaa.carcompanion.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.ganaa.carcompanion.BuildConfig
import com.ganaa.carcompanion.R
import com.tomtom.sdk.datamanagement.navigationtile.NavigationTileStore
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.navigation.TomTomNavigation
import com.tomtom.sdk.navigation.ui.NavigationFragment

import com.tomtom.sdk.routing.options.RoutePlanningOptions
import com.tomtom.sdk.routing.route.Route
import com.tomtom.sdk.map.display.ui.MapFragment as TomTomMapFragment
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.map.display.gesture.MapLongClickListener
import com.tomtom.sdk.map.display.route.Instruction
import com.tomtom.sdk.map.display.route.RouteOptions
import com.tomtom.sdk.routing.online.OnlineRoutePlanner
import com.tomtom.sdk.routing.RoutePlanner
import com.tomtom.sdk.routing.RoutePlanningCallback
import com.tomtom.sdk.routing.RoutePlanningResponse
import com.tomtom.sdk.routing.RoutingFailure
import com.tomtom.sdk.routing.options.Itinerary
import com.tomtom.sdk.routing.options.guidance.GuidanceOptions
import com.tomtom.sdk.vehicle.Vehicle

class MapFragment : Fragment() {

    private val apiKey = BuildConfig.TOMTOM_API_KEY

    private lateinit var tomTomMap: TomTomMap
    private lateinit var locationProvider: LocationProvider
    private lateinit var onLocationUpdateListener: OnLocationUpdateListener
    private lateinit var routePlanner: com.tomtom.sdk.routing.RoutePlanner
    private lateinit var routePlanningOptions: RoutePlanningOptions
    private lateinit var RoutePlanningCallback: RoutePlanningCallback
    private lateinit var RoutePlanningResponse: RoutePlanningResponse
    private lateinit var route:Route
    private lateinit var routingFailure: RoutingFailure
    private lateinit var RouteOptions:RouteOptions
    private lateinit var Instruction:Instruction
    private lateinit var MapLongClickListener:MapLongClickListener
    private lateinit var mapFragment: TomTomMapFragment
    private lateinit var supportFragmentManager: FragmentManager
    val searchApi = OnlineSearch.create(context, "YOUR_TOMTOM_API_KEY")



    interface RoutePlanner : AutoCloseable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }
    private fun initRouting() {
        routePlanner = OnlineRoutePlanner.create(
            context = requireContext().applicationContext,
            apiKey = apiKey
        )
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapOptions = MapOptions(apiKey)
        val tomTomMapFragment = TomTomMapFragment.newInstance(mapOptions)

        // Dynamically add TomTom map fragment to container
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, tomTomMapFragment)
            .commit()

        tomTomMapFragment.getMapAsync { map ->
            tomTomMap = map

            locationProvider = AndroidLocationProvider(requireContext())
            tomTomMap.setLocationProvider(locationProvider)

            if (areLocationPermissionsGranted()) {
                showUserLocation()
            } else {
                requestLocationPermissions()
            }
        }
    }


    private fun areLocationPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                showUserLocation()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun showUserLocation() {
        locationProvider.enable()

        onLocationUpdateListener = OnLocationUpdateListener { location ->
            tomTomMap.moveCamera(CameraOptions(location.position, zoom = 8.0))
            locationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
        }
        locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)

        val locationMarker = LocationMarkerOptions(type = LocationMarkerOptions.Type.Pointer)
        tomTomMap.enableLocationMarker(locationMarker)
    }

    private val routePlanningCallback =
        object : RoutePlanningCallback {
            override fun onSuccess(result: RoutePlanningResponse) {
                route = result.routes.first()
                route?.let { drawRoute(it) }
            }

            override fun onFailure(failure: RoutingFailure) {
                Toast.makeText(requireContext(), failure.message, Toast.LENGTH_SHORT).show()
            }

            override fun onRoutePlanned(route: Route) = Unit
        }

    private fun calculateRouteTo(destination: GeoPoint) {
        val userLocation =
            tomTomMap.currentLocation?.position ?: return
        val itinerary = Itinerary(origin = userLocation, destination = destination)
        routePlanningOptions =
            RoutePlanningOptions(
                itinerary = itinerary,
                guidanceOptions = GuidanceOptions(),
                vehicle = Vehicle.Car(),
            )
        routePlanner.planRoute(routePlanningOptions, routePlanningCallback)
    }

    private fun drawRoute(
        route: Route,
        color: Int = Color.RED,
        withDepartureMarker: Boolean = true,
        withZoom: Boolean = true,
    ) {
        val instructions =
            route.legs
                .flatMap { routeLeg -> routeLeg.instructions }
                .map {
                    Instruction(
                        routeOffset = it.routeOffset,
                    )
                }
        val routeOptions =
            RouteOptions(
                geometry = route.geometry,
                destinationMarkerVisible = true,
                departureMarkerVisible = withDepartureMarker,
                instructions = instructions,
                routeOffset = route.routePoints.map { it.routeOffset },
                color = color,
                tag = route.id.toString(),
            )
        tomTomMap.addRoute(routeOptions)
        if (withZoom) {
            tomTomMap.zoomToRoutes(ZOOM_TO_ROUTE_PADDING)
        }
    }
    companion object {
        private const val ZOOM_TO_ROUTE_PADDING = 100
    }
    private fun clearMap() {
        tomTomMap.clear()
    }

    private val mapLongClickListener =
        MapLongClickListener { geoPoint ->
            clearMap()
            calculateRouteTo(geoPoint)
            true
        }

    private fun setUpMapListeners() {
        tomTomMap.addMapLongClickListener(mapLongClickListener)
    }

    private fun initMap() {
        val mapOptions = MapOptions(mapKey = apiKey)
        mapFragment = TomTomMapFragment.newInstance(mapOptions)
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
        mapFragment.getMapAsync { map ->
            tomTomMap = map
            showUserLocation() // assuming this is your own method
            setUpMapListeners()
        }
    }


}
