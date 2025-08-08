package com.molytech.fsa.ui.servicios

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivitySolicitarServicioBinding
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
import java.util.Calendar

class SolicitarServicioActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var binding: ActivitySolicitarServicioBinding
    private lateinit var edtLatitud: EditText
    private lateinit var edtLongitud: EditText
    private lateinit var fecha: TextInputEditText
    private lateinit var hora: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var spnInventario: Spinner
    private lateinit var viewModel: SolicitarServicioViewModel
    private var marker: Marker? = null
    private var editingSolicitudId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySolicitarServicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupConfiguration()
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

    private fun setupConfiguration() {
        FirebaseApp.initializeApp(this)
    }

    private fun setupViewModel() {
        val viewModelFactory = SolicitarServicioViewModelFactory(
            SolicitudServicioDependencyProvider.createSolicitudUseCase,
            SolicitudServicioDependencyProvider.updateSolicitudUseCase,
            InventoryDependencyProvider.getActiveInventoryUseCase(this)
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[SolicitarServicioViewModel::class.java]
    }

    private fun setupViews() {
        edtLatitud = binding.editTextLatitud
        edtLongitud = binding.editTextLongitud
        fecha = binding.editTextFecha
        hora = binding.editTextHora
        btnGuardar = binding.btnGuardar
        spnInventario = binding.spnInventario
    }

    private fun setupMap() {
        mapView = binding.map
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
        btnGuardar.setOnClickListener {
            handleGuardarClick()
        }

        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        fecha.setOnClickListener {
            mostrarDatePickerDialog()
        }

        hora.setOnClickListener {
            mostrarTimePickerDialog()
        }

        binding.iconoExpandir.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivityForResult(intent, LOCATION_PERMISSION_REQUEST_CODE)
        }

        supportFragmentManager.setFragmentResultListener("requestKey", this) { _, bundle ->
            val latitude = bundle.getDouble("latitude")
            val longitude = bundle.getDouble("longitude")
            edtLatitud.setText(latitude.toString())
            edtLongitud.setText(longitude.toString())
        }
    }

    private fun loadIntentData() {
        val s_latitud = intent.getStringExtra("latitud")
        val s_longitud = intent.getStringExtra("longitud")
        val s_fecha = intent.getStringExtra("fecha")
        val s_hora = intent.getStringExtra("hora")
        val s_descripcion = intent.getStringExtra("descripcion")
        val s_inventario = intent.getStringExtra("inventario")
        editingSolicitudId = intent.getStringExtra("id")

        if (s_inventario != null && s_descripcion != null && s_hora != null &&
            s_fecha != null && s_latitud != null && s_longitud != null) {

            edtLatitud.setText(s_latitud)
            edtLongitud.setText(s_longitud)
            fecha.setText(s_fecha)
            hora.setText(s_hora)
            binding.editTextDescripcion.setText(s_descripcion)

            // El spinner se configurará cuando se cargue el inventario
            viewModel.inventarioItems.observe(this) { items ->
                val index = items.indexOf(s_inventario)
                if (index >= 0) {
                    spnInventario.setSelection(index)
                }
            }
        }
    }

    private fun handleGuardarClick() {
        val latitud = edtLatitud.text.toString()
        val longitud = edtLongitud.text.toString()
        val fechaSeleccionada = fecha.text.toString()
        val horaSeleccionada = hora.text.toString()
        val descripcion = binding.editTextDescripcion.text.toString()
        val inventario = spnInventario.selectedItem.toString()

        if (!viewModel.validateFields(latitud, longitud, fechaSeleccionada, horaSeleccionada, descripcion)) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val solCor = sharedPreferences.getString("usuCor", "") ?: ""
        val solUsuNom = sharedPreferences.getString("usuNom", "") ?: ""
        val solUsuTel = sharedPreferences.getString("usuTel", "") ?: ""

        val solicitud = SolicitudServicio(
            id = editingSolicitudId ?: "",
            correo = solCor,
            usuarioNombre = solUsuNom,
            usuarioTelefono = solUsuTel,
            descripcion = descripcion,
            estado = "A",
            fecha = fechaSeleccionada,
            hora = horaSeleccionada,
            inventario = inventario,
            latitud = latitud,
            longitud = longitud
        )

        if (editingSolicitudId == null) {
            viewModel.createSolicitud(solicitud)
        } else {
            viewModel.updateSolicitud(solicitud)
        }
    }

    private fun clearInputs() {
        edtLatitud.text.clear()
        edtLongitud.text.clear()
        fecha.text?.clear()
        hora.text?.clear()
        binding.editTextDescripcion.text?.clear()
        spnInventario.setSelection(0) // Reset to default option
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
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
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            R.style.CustomDatePickerTheme, { _, selectedYear, selectedMonth, selectedDay ->

            val selectedDate = "$selectedYear/${selectedMonth + 1}/$selectedDay" // Los meses empiezan desde 0, por eso +1
            fecha.setText(selectedDate)  // Asigna el valor al EditText
        }, year, month, day)

        datePickerDialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Aceptar") { dialog, _ ->
            val datePicker = (dialog as DatePickerDialog).datePicker
            val selectedDate = "${datePicker.dayOfMonth}/${datePicker.month + 1}/${datePicker.year}"
            fecha.setText(selectedDate)  // Asigna el valor al EditText
            dialog.dismiss()
        }

        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancelar") { dialog, _ ->
            dialog.dismiss()  // Cerrar el diálogo si el usuario cancela
        }

        datePickerDialog.show()
    }

    private fun mostrarTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            R.style.CustomDatePickerTheme,{ _, selectedHour, selectedMinute ->
            // Formatear la hora seleccionada y mostrarla en el EditText
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            hora.setText(selectedTime)  // Asigna el valor al EditText
        }, hour, minute, true) // true para formato 24 horas, false para formato AM/PM

        // Añadir botón de selección (Aceptar)
        timePickerDialog.setOnDismissListener {
            // El diálogo se cerrará automáticamente cuando se selecciona la hora.
            // Ya estamos obteniendo `selectedHour` y `selectedMinute` en el listener.
        }

        timePickerDialog.show()  // Mostrar el TimePicker
    }

}
