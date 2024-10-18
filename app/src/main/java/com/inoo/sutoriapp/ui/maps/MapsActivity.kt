package com.inoo.sutoriapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.pref.SessionViewModel
import com.inoo.sutoriapp.data.pref.SessionViewModelFactory
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.data.remote.response.story.ListStoryItem
import com.inoo.sutoriapp.databinding.ActivityMapsBinding
import com.inoo.sutoriapp.utils.Utils.showToast

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: MapsViewModel by viewModels()
    private lateinit var progressBar: ProgressBar

    private lateinit var pref : SutoriAppPreferences
    private var token: String? = null
    private var location: Int = 1

    private val sessionViewModel: SessionViewModel by viewModels{
        SessionViewModelFactory.getInstance(this, pref)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initializeVariables()
    }

    private fun initializeVariables() {
        val dataStore = this.applicationContext.dataStore
        pref = SutoriAppPreferences.getInstance(dataStore)

        sessionViewModel.getToken().observe(this) { token ->
            this.token = token
            setupView(token)
        }
    }

    private fun setupView(token: String) {
        mapsViewModel.fetchStoriesWithLocation(token, location)
        observeStories()
    }

    private fun observeStories() {
        mapsViewModel.storyList.observe(this) { stories ->
            stories?.forEach { story ->
                addMarkerForStory(story)
            }
        }
    }

    private fun observeLoading() {
        mapsViewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        }
    }

    private fun observeError() {
        mapsViewModel.error.observe(this) { error ->
            if (error != null) {
                showToast(this, getString(R.string.maps_error_message))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(CustomInfoWindowAdapter(this))
        getMyLocation()

        mMap.setOnMyLocationChangeListener { location ->
            val userLocation = LatLng(location.latitude, location.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
            mMap.addMarker(
                MarkerOptions()
                    .position(userLocation)
                    .title(getString(R.string.your_location))
            )
            mMap.setOnMyLocationChangeListener(null)
        }
        setMapStyle()
    }

    private fun addMarkerForStory(story: ListStoryItem) {
        val position = story.lon?.let { lon ->
            story.lat?.let { lat -> LatLng(lat, lon) }
        }

        position?.let { latLng ->
            val markerOptions = MarkerOptions()
                .position(latLng)
                .title(story.name)
                .icon(vectorToBitmap(R.drawable.ic_location_pin, Color.Red.toArgb()))

            val marker = mMap.addMarker(markerOptions)
            marker?.tag = story
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation(){
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
    companion object {
        private const val TAG = "MapsActivity"
    }
}