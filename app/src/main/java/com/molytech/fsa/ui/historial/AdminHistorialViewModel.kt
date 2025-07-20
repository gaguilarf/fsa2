package com.molytech.fsa.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.HistorialResult
import com.molytech.fsa.domain.entities.ServiceRequest
import com.molytech.fsa.domain.usecases.FilterServiceRequestsByStatusUseCase
import com.molytech.fsa.domain.usecases.GetAllServiceRequestsUseCase
import com.molytech.fsa.domain.usecases.SortServiceRequestsByDateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminHistorialViewModel(
    private val getAllServiceRequestsUseCase: GetAllServiceRequestsUseCase,
    private val filterServiceRequestsByStatusUseCase: FilterServiceRequestsByStatusUseCase,
    private val sortServiceRequestsByDateUseCase: SortServiceRequestsByDateUseCase
) : ViewModel() {

    private val _historialState = MutableStateFlow<HistorialResult>(HistorialResult.Loading)
    val historialState: StateFlow<HistorialResult> = _historialState

    private val _filteredRequests = MutableStateFlow<List<ServiceRequest>>(emptyList())
    val filteredRequests: StateFlow<List<ServiceRequest>> = _filteredRequests

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var allRequests: List<ServiceRequest> = emptyList()

    init {
        loadServiceRequests()
    }

    fun loadServiceRequests() {
        viewModelScope.launch {
            _isLoading.value = true
            _historialState.value = HistorialResult.Loading

            val result = getAllServiceRequestsUseCase()
            _historialState.value = result

            if (result is HistorialResult.Success) {
                allRequests = sortServiceRequestsByDateUseCase(result.requests)
                _filteredRequests.value = allRequests
            }

            _isLoading.value = false
        }
    }

    fun filterByStatus(status: String?) {
        val filtered = filterServiceRequestsByStatusUseCase(allRequests, status)
        _filteredRequests.value = filtered
    }

    fun refreshData() {
        loadServiceRequests()
    }

    fun getServiceRequestById(id: String): ServiceRequest? {
        return allRequests.find { it.id == id }
    }
}
