package com.molytech.fsa.ui.servicios

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.SolicitudServicio
import com.molytech.fsa.domain.usecases.CreateSolicitudUseCase
import com.molytech.fsa.domain.usecases.UpdateSolicitudUseCase
import com.molytech.fsa.domain.usecases.GetActiveInventoryUseCase
import kotlinx.coroutines.launch

class SolicitarServicioViewModel(
    private val createSolicitudUseCase: CreateSolicitudUseCase,
    private val updateSolicitudUseCase: UpdateSolicitudUseCase,
    private val getActiveInventoryUseCase: GetActiveInventoryUseCase
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    private val _inventarioItems = MutableLiveData<List<String>>()
    val inventarioItems: LiveData<List<String>> = _inventarioItems

    private val _operationCompleted = MutableLiveData<Boolean>()
    val operationCompleted: LiveData<Boolean> = _operationCompleted

    fun loadInventario() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = getActiveInventoryUseCase()
                result.fold(
                    onSuccess = { inventoryItems ->
                        val itemsList = mutableListOf<String>()
                        itemsList.add("Ninguno")
                        inventoryItems.forEachIndexed { index, item ->
                            itemsList.add("${index + 1}: ${item.description}       s/${item.price}")
                        }
                        _inventarioItems.value = itemsList
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = "Error al cargar el inventario: ${exception.message}"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun createSolicitud(solicitud: SolicitudServicio) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = createSolicitudUseCase(solicitud)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Solicitud: Operación exitosa"
                        _operationCompleted.value = true
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = "Solicitud: Operación fallida: ${exception.message}"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun updateSolicitud(solicitud: SolicitudServicio) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = updateSolicitudUseCase(solicitud)
                result.fold(
                    onSuccess = {
                        _successMessage.value = "Solicitud: Operación exitosa"
                        _operationCompleted.value = true
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = "Solicitud: Operación fallida: ${exception.message}"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = "Error inesperado: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun validateFields(
        latitud: String,
        longitud: String,
        fecha: String,
        hora: String,
        descripcion: String
    ): Boolean {
        return latitud.isNotEmpty() &&
               longitud.isNotEmpty() &&
               fecha.isNotEmpty() &&
               hora.isNotEmpty() &&
               descripcion.isNotEmpty()
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
