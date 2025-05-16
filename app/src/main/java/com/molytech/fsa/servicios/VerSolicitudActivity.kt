package com.molytech.fsa.servicios

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityVerSolicitudBinding
import com.molytech.fsa.screens.MapActivity
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class VerSolicitudActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerSolicitudBinding
    private lateinit var mapView: MapView
    private lateinit var edtLatitud: EditText
    private lateinit var edtLongitud: EditText
    private lateinit var fecha: TextInputEditText
    private lateinit var hora: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var swtEstado: MaterialSwitch
    private lateinit var edtNombre: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var btnLlamar: Button
    private lateinit var btnIr: Button
    private val db = FirebaseFirestore.getInstance()
    private var marker: Marker? = null
    private lateinit var spnInventario: Spinner
    private val inventarioList = mutableListOf<String>()
    var s_inventario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        setWindowColors()
        FirebaseApp.initializeApp(this)
        binding = ActivityVerSolicitudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.map
        mapView.setMultiTouchControls(true)

        val startPoint = GeoPoint(-16.4090, -71.5375) // Arequipa, Perú
        mapView.controller.setZoom(20.0) // Ajusta el nivel de zoom según lo necesites
        mapView.controller.setCenter(startPoint) // Establece el punto inicial del mapa

        marker = Marker(mapView)
        marker?.icon = ContextCompat.getDrawable(this, R.drawable.icon_marker) // Asegúrate de tener un drawable para el marcador
        marker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM) // Ancla el marcador en la parte inferior
        mapView.overlays.add(marker)

        edtLatitud = binding.editTextLatitud
        edtLongitud = binding.editTextLongitud
        val exp = binding.iconoExpandir
        fecha = binding.editTextFecha
        hora = binding.editTextHora
        swtEstado = binding.swtEstado
        edtNombre = binding.editTextNombre
        edtTelefono = binding.editTextTelefono
        btnLlamar = binding.btnLlamar
        btnIr = binding.btnAbrirMapa

        btnGuardar = binding.btnGuardar
        spnInventario = binding.spnInventario

        val s_correo = intent.getStringExtra("correo")
        val s_nombre = intent.getStringExtra("nombre")
        val s_telefono = intent.getStringExtra("telefono")
        val s_latitud = intent.getStringExtra("latitud")
        val s_longitud = intent.getStringExtra("longitud")
        val s_fecha = intent.getStringExtra("fecha")
        val s_hora = intent.getStringExtra("hora")
        val s_descripcion = intent.getStringExtra("descripcion")
        s_inventario = intent.getStringExtra("inventario")
        val s_estado = intent.getStringExtra("estado")
        var s_id = ""
        s_id = intent.getStringExtra("id").toString()
        cargarInventario()

        if(s_estado == "A"){
            swtEstado.isEnabled = true
        }

        btnLlamar.setOnClickListener {
            abrirAplicacionDeLlamadas(edtTelefono.text.toString())
        }

        btnIr.setOnClickListener {
            abrirMapa(edtLatitud.text.toString(), edtLongitud.text.toString())
        }

        btnGuardar.setOnClickListener {

            val solicitud = hashMapOf(
                "solCor" to s_correo, // Correo del usuario
                "solUsuNom" to s_nombre,
                "solUsuTel" to s_telefono,
                "solDes" to s_descripcion, // Descripción del problema con el auto
                "solEst" to "A", // El estado siempre será "A" para activo
                "solFec" to s_fecha, // Fecha seleccionada
                "solHor" to s_hora, // Hora seleccionada
                "solInv" to s_inventario, // Inventario seleccionado
                "solX" to s_latitud, // Latitud
                "solY" to s_longitud, // Longitud
                "solEst" to if (swtEstado.isChecked) "A" else "I"// Estado de la solicitud
            )

            db.collection("Solicitudes").document(s_id).set(solicitud)
                .addOnSuccessListener {

                    Toast.makeText(this, "Solicitud: Operación exitosa", Toast.LENGTH_SHORT).show()
                    clearInputs()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Solicitud: Operación fallida: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        // Finalizar la actividad actual
        finish()
        }

        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        exp.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAP)
        }

        // Configurar marcador y recibir eventos del mapa
        configurarEventosDelMapa()

        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val latitude = bundle.getDouble("latitude")
            val longitude = bundle.getDouble("longitude")
            edtLatitud.setText(latitude.toString())
            edtLongitud.setText(longitude.toString())
        }

        if (s_inventario != null && s_descripcion != null && s_hora != null && s_fecha != null && s_latitud != null && s_longitud != null) {
            edtLatitud.setText(s_latitud)
            edtLongitud.setText(s_longitud)
            fecha.setText(s_fecha)
            hora.setText(s_hora)
            edtNombre.setText(s_nombre)
            edtTelefono.setText(s_telefono)
            swtEstado.isChecked = s_estado == "A"
            binding.editTextDescripcion.setText(s_descripcion)
            spnInventario.setSelection(inventarioList.indexOf(s_inventario))
        }

    }

    private fun configurarEventosDelMapa() {
        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                // Mostrar la latitud y longitud de la ubicación seleccionada en EditTexts
                p?.let {
                    edtLatitud.setText(it.latitude.toString())
                    edtLongitud.setText(it.longitude.toString())
                    marker?.position = GeoPoint(it.latitude, it.longitude)
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

    private fun clearInputs() {
        edtLatitud.text.clear()
        edtLongitud.text.clear()
        fecha.text?.clear()
        hora.text?.clear()
        binding.editTextDescripcion.text?.clear()
        spnInventario.setSelection(0) // Reset to default option
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MAP && resultCode == RESULT_OK) {
            data?.let {
                val latitude = it.getDoubleExtra("latitude", 0.0)
                val longitude = it.getDoubleExtra("longitude", 0.0)

                // Actualizar los EditText con la nueva ubicación
                edtLatitud.setText(latitude.toString())
                edtLongitud.setText(longitude.toString())

                // Actualizar el MapView para centrar en la nueva ubicación
                val newPoint = GeoPoint(latitude, longitude)
                mapView.controller.setCenter(newPoint)
                mapView.controller.setZoom(20.0)

                // Actualizar la posición del marcador
                marker?.position = newPoint
                marker?.title = "Nueva ubicación seleccionada"
                marker?.showInfoWindow()
                mapView.invalidate() // Refrescar el mapa para mostrar los cambios
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_MAP = 100
    }

    private fun cargarInventario() {
        inventarioList.add("Ninguno")
        db.collection("Inventario")
            .whereEqualTo("invEst", "A")  // Filtrar por inventario activado
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    inventarioList.add(document.id + ": " + document.getString("invDes") + "       s/" + document.getString("invPre"))
                }
                // Crear un adaptador para el Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, inventarioList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnInventario.adapter = adapter

                // Seleccionar la segunda opción (índice 1)
                if (inventarioList.size > 1) {
                    spnInventario.setSelection(inventarioList.indexOf(s_inventario))
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar el inventario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setWindowColors() {
        val turquesaColor = ContextCompat.getColor(this, R.color.turquesa)
        window.statusBarColor = turquesaColor
        window.navigationBarColor = turquesaColor

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
    }

    private fun abrirAplicacionDeLlamadas(numero: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$numero")
        }
        startActivity(intent)
    }

    fun abrirMapa(latitude: String, longitude: String) {
        try {
            // Crear la URI concatenando las coordenadas
            val uri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")

            // Crear el Intent para abrir Google Maps
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Verificar si hay una aplicación que pueda manejar este Intent
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir Google Maps", Toast.LENGTH_SHORT).show()
        }
    }

}