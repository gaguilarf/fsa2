package com.molytech.fsa.inventario

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityAgregarInventarioBinding
import java.io.File

class agregarInventarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarInventarioBinding
    private lateinit var firestore: FirebaseFirestore
    private var imagenSeleccionadaUri: Uri? = null
    private var imagenCargadaUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowColors()
        binding = ActivityAgregarInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val edtNombre = binding.editTextNombreProducto
        val edtDescripcion = binding.editTextDescripcion
        val edtPrecio = binding.editTextPrecio
        val swtEstado = binding.swtEstado
        val btnSubirImagen = binding.btnSubirImagen
        val imgVista = binding.imgVista

        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val precio = intent.getStringExtra("precio")
        val urlImagen = intent.getStringExtra("imagen")
        val estado = intent.getStringExtra("estado")

        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        if (!urlImagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(urlImagen)
                .placeholder(R.drawable.img_preview)
                .error(R.drawable.ic_error)
                .into(imgVista)

            imagenCargadaUri = Uri.parse(urlImagen) // Solo se ejecuta si no es null ni vacío
        } else {
            imgVista.setImageResource(R.drawable.img_preview)
        }

        val seleccionarImagenLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data!!.data
                imagenSeleccionadaUri = uri
                imgVista.setImageURI(uri)
            }
        }

        btnSubirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            seleccionarImagenLauncher.launch(intent)
        }

        if (nombre != null && descripcion != null && precio != null && estado != null) {
            edtNombre.setText(nombre)
            edtDescripcion.setText(descripcion)
            edtPrecio.setText(precio)
            swtEstado.isChecked = estado == "A"
        }

        binding.btnGuardar.setOnClickListener {
            val nombreProducto = edtNombre.text.toString().trim()
            val descripcionProducto = edtDescripcion.text.toString().trim()
            val precioProducto = edtPrecio.text.toString().trim()
            val estadoProducto = if (swtEstado.isChecked) "A" else "I"

            if (nombreProducto.isEmpty() || descripcionProducto.isEmpty() || precioProducto.isEmpty()) {
                Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show()
            } else {
                if (imagenSeleccionadaUri != null && imagenSeleccionadaUri != imagenCargadaUri) {
                    subirImagenACloudinary(imagenSeleccionadaUri!!) { url ->
                        saveInventario(nombreProducto, descripcionProducto, precioProducto, estadoProducto, url)
                    }
                } else {
                    saveInventario(nombreProducto, descripcionProducto, precioProducto, estadoProducto, urlImagen ?: "")
                }
            }
        }
    }

    private fun saveInventario(nombre: String, descripcion: String, precio: String, estado: String, url: String) {
        val inventario = hashMapOf(
            "invDes" to descripcion,
            "invPre" to precio,
            "invUrl" to url,
            "invEst" to estado
        )

        firestore.collection("Inventario")
            .document(nombre)
            .set(inventario)
            .addOnSuccessListener {
                Toast.makeText(this, "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImagenACloudinary(uri: Uri, callback: (String) -> Unit) {
        Thread {
            try {
                val config = hashMapOf(
                    "cloud_name" to "dqlmsi4oj",
                    "api_key" to "727232429138216",
                    "api_secret" to "mg_UjUKk5hvEdt6OyIh4v3CBN0w"
                )
                val cloudinary = Cloudinary(config)
                val file = uriToFile(uri)
                val uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                val url = uploadResult["secure_url"] as String
                runOnUiThread { callback(url) }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)!!
        val file = File.createTempFile("temp_image", ".jpg", cacheDir)
        file.outputStream().use { inputStream.copyTo(it) }
        return file
    }

    private fun setWindowColors() {
        val turquesaColor = ContextCompat.getColor(this, R.color.turquesa)
        window.statusBarColor = turquesaColor
        window.navigationBarColor = turquesaColor
    }
}
