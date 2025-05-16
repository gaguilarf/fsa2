package com.molytech.fsa.screens

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityMapBinding
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapView: MapView
    private var edtLatitud: Double? = null
    private var edtLongitud: Double? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val turquesaColor = ContextCompat.getColor(this, R.color.turquesa)
        window.statusBarColor = turquesaColor
        window.navigationBarColor = turquesaColor

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // Íconos claros en barra de estado
            isAppearanceLightNavigationBars = false  // Íconos claros en barra de navegación
        }

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapFragmentView
        mapView.setMultiTouchControls(true)

        val startPoint = GeoPoint(-16.4090, -71.5375) // Arequipa, Perú
        mapView.controller.setZoom(20.0) // Ajusta el nivel de zoom según lo necesites
        mapView.controller.setCenter(startPoint) // Establece el punto inicial del mapa

        marker = Marker(mapView)
        marker?.icon = ContextCompat.getDrawable(this, R.drawable.icon_marker) // Asegúrate de tener un drawable para el marcador
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Ancla el marcador en la parte inferior
        mapView.overlays.add(marker)

        configurarEventosDelMapa()
        // Configura el botón para enviar las coordenadas
        val btnEnviar = binding.btnSeleccionar // Asegúrate de que tienes un botón en tu layout
        btnEnviar.setOnClickListener {
            enviarCoordenadas()
        }
    }

    private fun enviarCoordenadas() {
        if (edtLatitud != null && edtLongitud != null) {
            val intent = Intent()
            intent.putExtra("latitude", edtLatitud)
            intent.putExtra("longitude", edtLongitud)
            setResult(RESULT_OK, intent)
            finish() // Cierra la actividad y vuelve a SolicitarServicioActivity
        } else {
            // Manejar caso en el que no se seleccionó una ubicación
        }
    }
    private fun configurarEventosDelMapa() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                // Mostrar la latitud y longitud de la ubicación seleccionada en EditTexts
                p?.let {
                    edtLatitud = it.latitude
                    edtLongitud = it.longitude
                    marker?.position = GeoPoint(edtLatitud!!, edtLongitud!!)
                    marker?.title = "Ubicación seleccionada"
                    marker?.showInfoWindow()
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                // Manejar eventos de pulsación larga si es necesario
                return false
            }
        }

        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        mapView.overlays.add(mapEventsOverlay)
    }
}