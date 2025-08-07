package com.molytech.fsa.ui.map

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.molytech.fsa.R
import com.molytech.fsa.data.di.MapDependencyProvider
import com.molytech.fsa.databinding.ActivityMapBinding
import kotlinx.coroutines.launch
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var marker: Marker? = null

    private val viewModel: MapViewModel by viewModels {
        MapDependencyProvider.provideMapViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )
            windowInsets
        }

        setupMap()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupMap() {
        mapView = binding.mapFragmentView
        mapView.setMultiTouchControls(true)

        val startPoint = GeoPoint(-16.4090, -71.5375) // Arequipa, Perú
        mapView.controller.setZoom(20.0)
        mapView.controller.setCenter(startPoint)

        marker = Marker(mapView)
        marker?.icon = ContextCompat.getDrawable(this, R.drawable.icon_marker)
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        configurarEventosDelMapa()
    }

    private fun setupClickListeners() {
        binding.btnSeleccionar.setOnClickListener {
            enviarCoordenadas()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.selectedLocation.collect { mapPoint ->
                mapPoint?.let {
                    val coordinates = it.coordinates
                    marker?.position = GeoPoint(coordinates.latitude, coordinates.longitude)
                    marker?.title = it.title
                    marker?.showInfoWindow()
                    mapView.invalidate()
                }
            }
        }
    }

    private fun enviarCoordenadas() {
        val coordinates = viewModel.getSelectedCoordinates()
        if (coordinates != null) {
            val intent = Intent()
            intent.putExtra("latitude", coordinates.latitude)
            intent.putExtra("longitude", coordinates.longitude)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "Por favor selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarEventosDelMapa() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    viewModel.selectLocation(it.latitude, it.longitude)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }
}