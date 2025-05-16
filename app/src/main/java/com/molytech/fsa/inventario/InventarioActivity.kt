package com.molytech.fsa.inventario

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityInventarioBinding

class inventarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioBinding
    private lateinit var recyclerInventario: RecyclerView
    private lateinit var inventarioAdapter: InventarioAdapter
    private lateinit var items: MutableList<InventarioItem>
    private lateinit var db: FirebaseFirestore
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setWindowColors()
        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()

        // Configuración del RecyclerView
        recyclerInventario = binding.recyclerInventario
        recyclerInventario.layoutManager = LinearLayoutManager(this)
        items = mutableListOf() // Lista vacía

        // Inicializa el adaptador
        inventarioAdapter = InventarioAdapter(items) { selectedItem ->
            val intent = Intent(this, agregarInventarioActivity::class.java)
            intent.putExtra("nombre", selectedItem.nombre)
            intent.putExtra("descripcion", selectedItem.descripcion)
            intent.putExtra("precio", selectedItem.precio)
            intent.putExtra("estado", selectedItem.estado)
            addProductLauncher.launch(intent)
        }
        recyclerInventario.adapter = inventarioAdapter

        // Configurar el ActivityResultLauncher
        addProductLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Si se agregó o actualizó un producto, recargar los datos
                cargarInventarioDesdeFirestore()
            }
        }

        // Cargar datos desde Firestore
        cargarInventarioDesdeFirestore()

        // Acción del botón de retroceso
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        // Botón de agregar producto
        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, agregarInventarioActivity::class.java)
            addProductLauncher.launch(intent)
        }
    }

    private fun cargarInventarioDesdeFirestore() {
        db.collection("Inventario").get()
            .addOnSuccessListener { result ->
                items.clear() // Limpia la lista antes de recargar
                for (document in result) {
                    val nombre = document.id
                    val descripcion = document.getString("invDes") ?: ""
                    val precio = document.getString("invPre") ?: ""
                    val estado = document.getString("invEst") ?: ""

                    val item = InventarioItem(nombre, descripcion, precio, estado)
                    items.add(item)
                }
                // Notifica al adaptador que los datos han cambiado
                inventarioAdapter.notifyDataSetChanged()
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
