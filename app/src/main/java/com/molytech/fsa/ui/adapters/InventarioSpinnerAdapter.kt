package com.molytech.fsa.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.molytech.fsa.R
import com.molytech.fsa.domain.entities.InventarioItem

class InventarioSpinnerAdapter(
    private val context: Context,
    private val items: List<InventarioItem>
) : BaseAdapter() {

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): InventarioItem = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_inventario_spinner,
            parent,
            false
        )

        val item = items[position]

        val imgVista = view.findViewById<ImageView>(R.id.imgVista)
        val txtNombre = view.findViewById<TextView>(R.id.txtNombre)
        val txtPrecio = view.findViewById<TextView>(R.id.txtPrecio)

        txtNombre.text = item.nombre
        txtPrecio.text = "S/. ${item.precio}"

        // Cargar imagen usando Glide
        if (item.imagen.isNotEmpty()) {
            Glide.with(context)
                .load(item.imagen)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(imgVista)
        } else {
            imgVista.setImageResource(R.drawable.ic_launcher_foreground)
        }

        return view
    }
}
