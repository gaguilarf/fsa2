package com.molytech.fsa.ui.historial

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.HistorialItem
import com.molytech.fsa.domain.usecases.GetHistorialServiciosUseCase
import kotlinx.coroutines.launch

class HistorialServiciosViewModel(
    private val getHistorialServiciosUseCase: GetHistorialServiciosUseCase
) : ViewModel() {

    private val _historialItems = MutableLiveData<List<HistorialItem>>()
    val historialItems: LiveData<List<HistorialItem>> = _historialItems

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadHistorial(userEmail: String) {
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val result = getHistorialServiciosUseCase(userEmail)
                result.fold(
                    onSuccess = { items ->
                        _historialItems.value = items
                        _isLoading.value = false
                    },
                    onFailure = { exception ->
                        _error.value = exception.message ?: "Error desconocido al cargar el historial"
                        _isLoading.value = false
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
