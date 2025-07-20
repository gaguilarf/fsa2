package com.molytech.fsa.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.domain.entities.HistorialItem
import com.molytech.fsa.domain.repositories.HistorialRepository
import kotlinx.coroutines.tasks.await

class HistorialRepositoryImpl(
    private val firestore: FirebaseFirestore
) : HistorialRepository {

    override suspend fun getHistorialByUserEmail(email: String): Result<List<HistorialItem>> {
        return try {
            val querySnapshot = firestore.collection("Solicitudes")
                .whereEqualTo("solCor", email)
                .get()
                .await()

            val historialItems = querySnapshot.documents.mapNotNull { document ->
                try {
                    HistorialItem(
                        id = document.id,
                        correo = document.getString("solCor") ?: "",
                        hora = document.getString("solHor") ?: "",
                        fecha = document.getString("solFec") ?: "",
                        nombre = document.getString("solUsuNom") ?: "",
                        telefono = document.getString("solUsuTel") ?: "",
                        descripcion = document.getString("solDes") ?: "",
                        inventario = document.getString("solInv") ?: "",
                        latitud = document.getString("solX") ?: "",
                        longitud = document.getString("solY") ?: "",
                        estado = document.getString("solEst") ?: ""
                    )
                } catch (e: Exception) {
                    null // Ignorar documentos mal formateados
                }
            }

            Result.success(historialItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
