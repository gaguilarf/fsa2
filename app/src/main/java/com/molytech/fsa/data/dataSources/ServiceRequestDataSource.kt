package com.molytech.fsa.data.dataSources

import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.domain.entities.ServiceRequest
import kotlinx.coroutines.tasks.await

interface ServiceRequestDataSource {
    suspend fun getAllServiceRequests(): List<ServiceRequest>
    suspend fun getServiceRequestById(id: String): ServiceRequest?
    suspend fun updateServiceRequestStatus(id: String, status: String): Boolean
}

class ServiceRequestDataSourceImpl(
    private val firestore: FirebaseFirestore
) : ServiceRequestDataSource {

    override suspend fun getAllServiceRequests(): List<ServiceRequest> {
        return try {
            val result = firestore.collection("Solicitudes")
                .orderBy("solFec")
                .get()
                .await()

            result.documents.map { document ->
                ServiceRequest(
                    id = document.id,
                    userEmail = document.getString("solCor") ?: "",
                    userName = document.getString("solUsuNom") ?: "",
                    userPhone = document.getString("solUsuTel") ?: "",
                    description = document.getString("solDes") ?: "",
                    date = document.getString("solFec") ?: "",
                    time = document.getString("solHor") ?: "",
                    inventory = document.getString("solInv") ?: "",
                    latitude = document.getString("solX") ?: "",
                    longitude = document.getString("solY") ?: "",
                    status = document.getString("solEst") ?: ""
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getServiceRequestById(id: String): ServiceRequest? {
        return try {
            val document = firestore.collection("Solicitudes")
                .document(id)
                .get()
                .await()

            if (document.exists()) {
                ServiceRequest(
                    id = document.id,
                    userEmail = document.getString("solCor") ?: "",
                    userName = document.getString("solUsuNom") ?: "",
                    userPhone = document.getString("solUsuTel") ?: "",
                    description = document.getString("solDes") ?: "",
                    date = document.getString("solFec") ?: "",
                    time = document.getString("solHor") ?: "",
                    inventory = document.getString("solInv") ?: "",
                    latitude = document.getString("solX") ?: "",
                    longitude = document.getString("solY") ?: "",
                    status = document.getString("solEst") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateServiceRequestStatus(id: String, status: String): Boolean {
        return try {
            firestore.collection("Solicitudes")
                .document(id)
                .update("solEst", status)
                .await()
            true
        } catch (e: Exception) {
            throw e
        }
    }
}
