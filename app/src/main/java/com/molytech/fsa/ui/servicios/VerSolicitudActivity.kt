package com.molytech.fsa.ui.servicios

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityVerSolicitudBinding
import com.molytech.fsa.ui.map.MapActivity
import com.molytech.fsa.domain.entities.SolicitudServicio
import com.molytech.fsa.data.di.SolicitudServicioDependencyProvider
import com.molytech.fsa.data.di.InventoryDependencyProvider
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
    private lateinit var spnInventario: Spinner
    private lateinit var viewModel: VerSolicitudViewModel
    private var marker: Marker? = null
    private var currentSolicitud: SolicitudServicio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        binding = ActivityVerSolicitudBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupViewModel()
        setupViews()
        setupMap()
        setupObservers()
        setupClickListeners()
        loadIntentData()

        viewModel.loadInventario()
    }

    private fun setupWindow() {
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

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
    }

    private fun setupViewModel() {
        val viewModelFactory = VerSolicitudViewModelFactory(
            SolicitudServicioDependencyProvider.updateSolicitudUseCase,
            SolicitudServicioDependencyProvider.getSolicitudByIdUseCase,
            InventoryDependencyProvider.getActiveInventoryUseCase(this)
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[VerSolicitudViewModel::class.java]
    }

    private fun setupViews() {
        mapView = binding.map
        edtLatitud = binding.editTextLatitud
        edtLongitud = binding.editTextLongitud
        fecha = binding.editTextFecha
        hora = binding.editTextHora
        swtEstado = binding.swtEstado
        edtNombre = binding.editTextNombre
        edtTelefono = binding.editTextTelefono
        btnLlamar = binding.btnLlamar
        btnIr = binding.btnAbrirMapa
        btnGuardar = binding.btnGuardar
        spnInventario = binding.spnInventario
    }

    private fun setupMap() {
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

    private fun setupObservers() {
        viewModel.inventarioItems.observe(this) { items ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnInventario.adapter = adapter
        }

        viewModel.solicitud.observe(this) { solicitud ->
            solicitud?.let {
                currentSolicitud = it
                populateFields(it)
            }
        }

        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.operationCompleted.observe(this) { completed ->
            if (completed) {
                clearInputs()
                finish()
            }
        }
    }

    private fun setupClickListeners() {
        btnLlamar.setOnClickListener {
            abrirAplicacionDeLlamadas(edtTelefono.text.toString())
        }

        btnIr.setOnClickListener {
            abrirMapa(edtLatitud.text.toString(), edtLongitud.text.toString())
        }

        btnGuardar.setOnClickListener {
            handleGuardarClick()
        }

        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        binding.iconoExpandir.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_MAP)
        }

        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val latitude = bundle.getDouble("latitude")
            val longitude = bundle.getDouble("longitude")
            edtLatitud.setText(latitude.toString())
            edtLongitud.setText(longitude.toString())
        }
    }

    private fun loadIntentData() {
        val s_correo = intent.getStringExtra("correo")
        val s_nombre = intent.getStringExtra("nombre")
        val s_telefono = intent.getStringExtra("telefono")
        val s_latitud = intent.getStringExtra("latitud")
        val s_longitud = intent.getStringExtra("longitud")
        val s_fecha = intent.getStringExtra("fecha")
        val s_hora = intent.getStringExtra("hora")
        val s_descripcion = intent.getStringExtra("descripcion")
        val s_inventario = intent.getStringExtra("inventario")
        val s_estado = intent.getStringExtra("estado")
        val s_id = intent.getStringExtra("id")

        // Configurar estado del switch según el estado actual
        if (s_estado == "A") {
            swtEstado.isEnabled = true
        }

        // Crear solicitud con los datos del intent
        if (s_id != null && s_correo != null && s_nombre != null) {
            currentSolicitud = SolicitudServicio(
                id = s_id,
                correo = s_correo,
                usuarioNombre = s_nombre,
                usuarioTelefono = s_telefono ?: "",
                descripcion = s_descripcion ?: "",
                estado = s_estado ?: "",
                fecha = s_fecha ?: "",
                hora = s_hora ?: "",
                inventario = s_inventario ?: "",
                latitud = s_latitud ?: "",
                longitud = s_longitud ?: ""
            )

            populateFields(currentSolicitud!!)
        }
    }

    private fun populateFields(solicitud: SolicitudServicio) {
        edtLatitud.setText(solicitud.latitud)
        edtLongitud.setText(solicitud.longitud)
        fecha.setText(solicitud.fecha)
        hora.setText(solicitud.hora)
        edtNombre.setText(solicitud.usuarioNombre)
        edtTelefono.setText(solicitud.usuarioTelefono)
        swtEstado.isChecked = solicitud.estado == "A"
        binding.editTextDescripcion.setText(solicitud.descripcion)

        updateSwitchColor()

        swtEstado.setOnCheckedChangeListener { _, isChecked ->
            updateSwitchColor()
        }

        // Configurar spinner cuando se cargue el inventario
        viewModel.inventarioItems.observe(this) { items ->
            val index = items.indexOf(solicitud.inventario)
            if (index >= 0) {
                spnInventario.setSelection(index)
            }
        }
    }

    private fun updateSwitchColor() {
        if (swtEstado.isChecked) {
            // Color turquesa cuando está activado
            swtEstado.thumbTintList = ContextCompat.getColorStateList(this, R.color.turquesa)
        } else {
            // Color gris por defecto cuando está desactivado
            swtEstado.thumbTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        }
    }

    private fun handleGuardarClick() {
        currentSolicitud?.let { solicitud ->
            val nuevoEstado = if (swtEstado.isChecked) "A" else "I"
            viewModel.updateSolicitudEstado(solicitud, nuevoEstado)
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
