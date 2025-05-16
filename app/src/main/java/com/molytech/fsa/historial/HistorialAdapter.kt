package com.molytech.fsa.historial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.molytech.fsa.R

class HistorialAdapter(

    private val itemList: List<HistorialItem>,
    private val onItemClick: (HistorialItem) -> Unit

) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {
    class HistorialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtTelefono: TextView = itemView.findViewById(R.id.txtTelefono)
        val txtHora: TextView = itemView.findViewById(R.id.txtHora)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial, parent, false)
        return HistorialViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.txtHora.text = currentItem.hora
        holder.txtFecha.text = currentItem.fecha
        holder.txtNombre.text = currentItem.nombre
        holder.txtTelefono.text = currentItem.telefono

        // Asigna el color del texto según el estado
        if (currentItem.estado == "A") {
            holder.txtEstado.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.verde
            ))
        } else {
            holder.txtEstado.setTextColor(ContextCompat.getColor(holder.itemView.context,
                R.color.amarillo
            ))
        }
        holder.txtEstado.text = currentItem.estado

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = itemList.size

}