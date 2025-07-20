package com.molytech.fsa.domain.entities

data class SolicitudServicio(
    val id: String = "",
    val correo: String,
    val usuarioNombre: String,
    val usuarioTelefono: String,
    val descripcion: String,
    val estado: String,
    val fecha: String,
    val hora: String,
    val inventario: String,
    val latitud: String,
    val longitud: String
)
