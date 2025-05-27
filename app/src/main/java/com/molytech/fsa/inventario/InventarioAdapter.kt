package com.molytech.fsa.inventario

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.molytech.fsa.R
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class InventarioAdapter(
    private val itemList: List<InventarioItem>,
    private val onItemClick: (InventarioItem) -> Unit  // Función para manejar clics
) : RecyclerView.Adapter<InventarioAdapter.InventarioViewHolder>() {

    class InventarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcion)
        val txtPrecio: TextView = itemView.findViewById(R.id.txtPrecio)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val imgVista: ImageView = itemView.findViewById(R.id.imgVista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventario, parent, false)
        return InventarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventarioViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.txtNombre.text = currentItem.nombre
        holder.txtDescripcion.text = currentItem.descripcion
        holder.txtPrecio.text = "s/${currentItem.precio}"
        // Cargar la imagen en el imgVista usando una url con bordes redondos
        if (!currentItem.imagen.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentItem.imagen)
                .placeholder(R.drawable.subir)
                .error(R.drawable.ic_error)
                .centerCrop()
                .transform(
                    RoundedCornersTransformation(
                        45,
                        0,
                        RoundedCornersTransformation.CornerType.LEFT
                    )
                )
                .into(holder.imgVista)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.ic_error)
                .centerCrop()
                .transform(
                    RoundedCornersTransformation(
                        45,
                        0,
                        RoundedCornersTransformation.CornerType.LEFT
                    )
                )
                .into(holder.imgVista)
        }



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

        // Clic en cada item para abrir agregarInventarioActivity con los datos
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = itemList.size
}

