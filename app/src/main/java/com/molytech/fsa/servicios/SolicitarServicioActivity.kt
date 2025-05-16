package com.molytech.fsa.servicios

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivitySolicitarServicioBinding
import com.molytech.fsa.screens.MapActivity
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
    private val db = FirebaseFirestore.getInstance()
    private var marker: Marker? = null
    private lateinit var spnInventario: Spinner
    private val inventarioList = mutableListOf<String>()
    var s_inventario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        val turquesaColor = ContextCompat.getColor(this, R.color.turquesa)
        window.statusBarColor = turquesaColor
        window.navigationBarColor = turquesaColor

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // Íconos claros en barra de estado
            isAppearanceLightNavigationBars = false  // Íconos claros en barra de navegación
        }

        FirebaseApp.initializeApp(this)
        binding = ActivitySolicitarServicioBinding.inflate(layoutInflater)
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
        btnGuardar = binding.btnGuardar
        spnInventario = binding.spnInventario

        val s_latitud = intent.getStringExtra("latitud")
        val s_longitud = intent.getStringExtra("longitud")
        val s_fecha = intent.getStringExtra("fecha")
        val s_hora = intent.getStringExtra("hora")
        val s_descripcion = intent.getStringExtra("descripcion")
        s_inventario = intent.getStringExtra("inventario")
        var s_id = intent.getStringExtra("id")
        cargarInventario()

        btnGuardar.setOnClickListener {
            // Obtener los valores de los EditText
            val latitud = edtLatitud.text.toString()
            val longitud = edtLongitud.text.toString()
            val fechaSeleccionada = fecha.text.toString()
            val horaSeleccionada = hora.text.toString()
            val descripcion = binding.editTextDescripcion.text.toString()
            val inventario = spnInventario.selectedItem.toString()

            // Validar que los campos no estén vacíos
            if (latitud.isEmpty() || longitud.isEmpty() || fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty() || descripcion.isEmpty()) {
                // Mostrar mensaje de error si algún campo está vacío
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {

                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                val solCor = sharedPreferences.getString("usuCor", "")
                val solUsuNom = sharedPreferences.getString("usuNom", "")
                val solUsuTel = sharedPreferences.getString("usuTel", "")

                val solicitud = hashMapOf(
                    "solCor" to solCor, // Correo del usuario
                    "solUsuNom" to solUsuNom,
                    "solUsuTel" to solUsuTel,
                    "solDes" to descripcion, // Descripción del problema con el auto
                    "solEst" to "A", // El estado siempre será "A" para activo
                    "solFec" to fechaSeleccionada, // Fecha seleccionada
                    "solHor" to horaSeleccionada, // Hora seleccionada
                    "solInv" to inventario, // Inventario seleccionado
                    "solX" to latitud, // Latitud
                    "solY" to longitud // Longitud
                )
                val saveRequestOperation = if (s_id == null) {
                    db.collection("Solicitudes").add(solicitud)
                } else {
                    db.collection("Solicitudes").document(s_id).set(solicitud)
                }

                saveRequestOperation
                    .addOnSuccessListener {
                        Toast.makeText(this, "Solicitud: Operación exitosa", Toast.LENGTH_SHORT).show()
                        clearInputs()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Solicitud: Operación fallida: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }

            // Finalizar la actividad actual
            finish()
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

        exp.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivityForResult(intent, LOCATION_PERMISSION_REQUEST_CODE)
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
            binding.editTextDescripcion.setText(s_descripcion)
            spnInventario.setSelection(inventarioList.indexOf(s_inventario))
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



}
