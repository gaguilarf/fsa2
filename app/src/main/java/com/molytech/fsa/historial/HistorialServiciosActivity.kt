package com.molytech.fsa.historial

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.servicios.SolicitarServicioActivity
import com.molytech.fsa.databinding.ActivityHistorialServiciosBinding

class HistorialServiciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialServiciosBinding
    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var items: MutableList<HistorialItem>
    private lateinit var db: FirebaseFirestore
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>
    private lateinit var sharedPreferences: SharedPreferences
    private var correo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowColors()
        FirebaseApp.initializeApp(this)
        binding = ActivityHistorialServiciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        recyclerHistorial = binding.recyclerHistorial
        recyclerHistorial.layoutManager = LinearLayoutManager(this)
        items = mutableListOf() // Lista vacía

        // Inicializa el adaptador
        historialAdapter = HistorialAdapter(items) { selectedItem ->
            val intent = Intent(this, SolicitarServicioActivity::class.java)
            intent.putExtra("id", selectedItem.id)
            intent.putExtra("hora", selectedItem.hora)
            intent.putExtra("fecha", selectedItem.fecha)
            intent.putExtra("nombre", selectedItem.nombre)
            intent.putExtra("telefono", selectedItem.telefono)
            intent.putExtra("descripcion", selectedItem.descripcion)
            intent.putExtra("inventario", selectedItem.inventario)
            intent.putExtra("latitud", selectedItem.latitud)
            intent.putExtra("longitud", selectedItem.longitud)
            intent.putExtra("estado", selectedItem.estado)
            addProductLauncher.launch(intent)
        }
        recyclerHistorial.adapter = historialAdapter
        correo = sharedPreferences.getString("usuCor", "")
        // Configurar el ActivityResultLauncher
        addProductLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Si se agregó o actualizó un producto, recargar los datos
                cargarHistorialDesdeFirestore()
            }
        }

        // Cargar datos desde Firestore
        cargarHistorialDesdeFirestore()

        // Acción del botón de retroceso
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        // Botón de agregar producto
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, SolicitarServicioActivity::class.java)
            addProductLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarHistorialDesdeFirestore()
    }
    private fun cargarHistorialDesdeFirestore() {
        db.collection("Solicitudes").get()
            .addOnSuccessListener { result ->
                items.clear() // Limpia la lista antes de recargar
                for (document in result) {
                    if(document.getString("solCor") == correo){
                        val id = document.id
                        val correo = document.getString("solCor") ?: ""
                        val fecha = document.getString("solFec") ?: ""
                        val hora = document.getString("solHor") ?: ""
                        val nombre = document.getString("solUsuNom") ?: ""
                        val telefono = document.getString("solUsuTel") ?: ""
                        val descripcion = document.getString("solDes") ?: ""
                        val inventario = document.getString("solInv") ?: ""
                        val latitud = document.getString("solX") ?: ""
                        val longitud = document.getString("solY") ?: ""
                        val estado = document.getString("solEst") ?: ""
                        val item = HistorialItem(id, correo, hora, fecha, nombre, telefono, descripcion, inventario, latitud, longitud, estado)
                        items.add(item)
                    }
                }
                // Notifica al adaptador que los datos han cambiado
                historialAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Manejar errores
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
}