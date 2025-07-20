package com.molytech.fsa.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.domain.entities.SolicitudServicio
import com.molytech.fsa.domain.repositories.SolicitudServicioRepository
import kotlinx.coroutines.tasks.await

class SolicitudServicioRepositoryImpl(
    private val firestore: FirebaseFirestore
) : SolicitudServicioRepository {

    override suspend fun createSolicitud(solicitud: SolicitudServicio): Result<String> {
        return try {
            val solicitudData = hashMapOf<String, Any>(
                "solCor" to solicitud.correo,
                "solUsuNom" to solicitud.usuarioNombre,
                "solUsuTel" to solicitud.usuarioTelefono,
                "solDes" to solicitud.descripcion,
                "solEst" to solicitud.estado,
                "solFec" to solicitud.fecha,
                "solHor" to solicitud.hora,
                "solInv" to solicitud.inventario,
                "solX" to solicitud.latitud,
                "solY" to solicitud.longitud
            )

            val documentReference = firestore.collection("Solicitudes")
                .add(solicitudData)
                .await()

            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateSolicitud(solicitud: SolicitudServicio): Result<Unit> {
        return try {
            val solicitudData = hashMapOf<String, Any>(
                "solCor" to solicitud.correo,
                "solUsuNom" to solicitud.usuarioNombre,
                "solUsuTel" to solicitud.usuarioTelefono,
                "solDes" to solicitud.descripcion,
                "solEst" to solicitud.estado,
                "solFec" to solicitud.fecha,
                "solHor" to solicitud.hora,
                "solInv" to solicitud.inventario,
                "solX" to solicitud.latitud,
                "solY" to solicitud.longitud
            )

            firestore.collection("Solicitudes")
                .document(solicitud.id)
                .set(solicitudData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSolicitudById(id: String): Result<SolicitudServicio> {
        return try {
            val document = firestore.collection("Solicitudes")
                .document(id)
                .get()
                .await()

            if (document.exists()) {
                val solicitud = SolicitudServicio(
                    id = document.id,
                    correo = document.getString("solCor") ?: "",
                    usuarioNombre = document.getString("solUsuNom") ?: "",
                    usuarioTelefono = document.getString("solUsuTel") ?: "",
                    descripcion = document.getString("solDes") ?: "",
                    estado = document.getString("solEst") ?: "",
                    fecha = document.getString("solFec") ?: "",
                    hora = document.getString("solHor") ?: "",
                    inventario = document.getString("solInv") ?: "",
                    latitud = document.getString("solX") ?: "",
                    longitud = document.getString("solY") ?: ""
                )
                Result.success(solicitud)
            } else {
                Result.failure(Exception("Solicitud no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
