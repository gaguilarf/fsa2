package com.molytech.fsa.domain.repositories

import com.molytech.fsa.domain.entities.HistorialItem

interface HistorialRepository {
    suspend fun getHistorialByUserEmail(email: String): Result<List<HistorialItem>>
}
